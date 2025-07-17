package kr.co.bookquiz.api.service

import kr.co.bookquiz.api.api.exception.EntityNotFoundException
import kr.co.bookquiz.api.api.exception.ErrorCode
import kr.co.bookquiz.api.dto.book.BookCreateRequest
import kr.co.bookquiz.api.dto.book.BookResponse
import kr.co.bookquiz.api.dto.book.BookUpdateRequest
import kr.co.bookquiz.api.dto.book.toEntity
import kr.co.bookquiz.api.dto.book.toResponse
import kr.co.bookquiz.api.entity.Author
import kr.co.bookquiz.api.entity.Book
import kr.co.bookquiz.api.entity.Illustrator
import kr.co.bookquiz.api.entity.Translator
import kr.co.bookquiz.api.repository.AuthorRepository
import kr.co.bookquiz.api.repository.BookRepository
import kr.co.bookquiz.api.repository.IllustratorRepository
import kr.co.bookquiz.api.repository.TranslatorRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
    private val translatorRepository: TranslatorRepository,
    private val illustratorRepository: IllustratorRepository
) {

    fun createBook(bookCreateRequest: BookCreateRequest): BookResponse {
        // Find or create authors
        val authors = bookCreateRequest.authorNames.map { name ->
            authorRepository.findByName(name).orElseGet {
                authorRepository.save(Author(name = name))
            }
        }

        // Find or create translators
        val translators = bookCreateRequest.translatorNames.map { name ->
            translatorRepository.findByName(name).orElseGet {
                translatorRepository.save(Translator(name = name))
            }
        }

        // Find or create illustrators
        val illustrators = bookCreateRequest.illustratorNames.map { name ->
            illustratorRepository.findByName(name).orElseGet {
                illustratorRepository.save(Illustrator(name = name))
            }
        }

        val bookEntity = bookCreateRequest.toEntity()
        val newBook = bookEntity.copy(authors = authors, translators = translators, illustrators = illustrators)
        val savedBook = bookRepository.save(newBook)
        return savedBook.toResponse()
    }

    fun getBookById(id: Long): BookResponse {
        val book = getBookEntityById(id)
        return book.toResponse()
    }

    private fun getBookEntityById(id: Long): Book {
        return bookRepository.findById(id).orElseThrow {
            EntityNotFoundException("Book", listOf(id.toString()), ErrorCode.BOOK_NOT_FOUND)
        }
    }

    fun updateBook(id: Long, bookUpdateRequest: BookUpdateRequest): BookResponse {
        val existingBook = getBookEntityById(id)

        // Store old entities for cleanup
        val oldAuthors = existingBook.authors
        val oldTranslators = existingBook.translators
        val oldIllustrators = existingBook.illustrators

        // Find or create new authors
        val authors = bookUpdateRequest.authorNames.map { name ->
            authorRepository.findByName(name).orElseGet {
                authorRepository.save(Author(name = name))
            }
        }

        // Find or create new translators
        val translators = bookUpdateRequest.translatorNames.map { name ->
            translatorRepository.findByName(name).orElseGet {
                translatorRepository.save(Translator(name = name))
            }
        }

        // Find or create new illustrators
        val illustrators = bookUpdateRequest.illustratorNames.map { name ->
            illustratorRepository.findByName(name).orElseGet {
                illustratorRepository.save(Illustrator(name = name))
            }
        }

        val updatedBook = existingBook.copy(
            title = bookUpdateRequest.title,
            isbn = bookUpdateRequest.isbn,
            publisher = bookUpdateRequest.publisher,
            quizPrice = bookUpdateRequest.quizPrice,
            thumbnail = bookUpdateRequest.thumbnail,
            authors = authors,
            translators = translators,
            illustrators = illustrators
        )
        val savedBook = bookRepository.save(updatedBook)

        // Clean up unused entities
        cleanupUnusedAuthors(oldAuthors, authors)
        cleanupUnusedTranslators(oldTranslators, translators)
        cleanupUnusedIllustrators(oldIllustrators, illustrators)

        return savedBook.toResponse()
    }

    private fun cleanupUnusedAuthors(oldAuthors: List<Author>, newAuthors: List<Author>?) {

        val removedAuthors : List<Author> = if (newAuthors != null) {
            oldAuthors.filter { oldAuthor ->
                newAuthors.none { newAuthor -> newAuthor.id == oldAuthor.id }
            }
        } else {
            oldAuthors
        }
        
        removedAuthors.forEach { author ->
            author.id?.let { authorId ->
                if (authorRepository.countBooksByAuthorId(authorId) == 0L) {
                    authorRepository.deleteById(authorId)
                }
            }
        }
    }

    private fun cleanupUnusedTranslators(oldTranslators: List<Translator>, newTranslators: List<Translator>?) {
      val removedTranslators: List<Translator> =
      if (newTranslators != null){
        oldTranslators.filter { oldTranslator ->
            newTranslators.none { newTranslator -> newTranslator.id == oldTranslator.id }
        }
      }
      else {
        oldTranslators
      }
        
      removedTranslators.forEach { translator ->
          translator.id?.let { translatorId ->
              if (translatorRepository.countBooksByTranslatorId(translatorId) == 0L) {
                  translatorRepository.deleteById(translatorId)
              }
          }
      }
    }

    private fun cleanupUnusedIllustrators(oldIllustrators: List<Illustrator>, newIllustrators: List<Illustrator>?) {
        val removedIllustrators : List<Illustrator> = if (newIllustrators != null) {
            oldIllustrators.filter { oldIllustrator ->
                newIllustrators.none { newIllustrator -> newIllustrator.id == oldIllustrator.id }
            }
        } else {
            oldIllustrators
        }
        
        removedIllustrators.forEach { illustrator ->
            illustrator.id?.let { illustratorId ->
                if (illustratorRepository.countBooksByIllustratorId(illustratorId) == 0L) {
                    illustratorRepository.deleteById(illustratorId)
                }
            }
        }
    }

    fun deleteBook(id: Long) {
        val existingBook = getBookEntityById(id)
        
        // Store entities for cleanup before deletion
        val oldAuthors = existingBook.authors
        val oldTranslators = existingBook.translators
        val oldIllustrators = existingBook.illustrators
        
        // Delete the book first
        bookRepository.deleteById(id)
        
        // Clean up unused entities (passing null as newEntities to delete all that are unused)
        cleanupUnusedAuthors(oldAuthors, null)
        cleanupUnusedTranslators(oldTranslators, null)
        cleanupUnusedIllustrators(oldIllustrators, null)
    }
}