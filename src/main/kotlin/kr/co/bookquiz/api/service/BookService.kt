package kr.co.bookquiz.api.service

import kr.co.bookquiz.api.api.exception.EntityNotFoundException
import kr.co.bookquiz.api.api.exception.ErrorCode
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

    fun createBook(book: Book, authorIds: List<String>, translatorIds: List<String>, illustratorIds: List<String>): Book {
        // Validate authors
        val authors = authorRepository.findAllById(authorIds)
        val foundAuthorIds = authors.map { it.id }
        val missingAuthorIds = authorIds.filter { it !in foundAuthorIds }
        if (missingAuthorIds.isNotEmpty()) {
            throw EntityNotFoundException("Author", missingAuthorIds, ErrorCode.AUTHOR_NOT_FOUND)
        }

        // Validate translators
        val translators = translatorRepository.findAllById(translatorIds)
        val foundTranslatorIds = translators.map { it.id }
        val missingTranslatorIds = translatorIds.filter { it !in foundTranslatorIds }
        if (missingTranslatorIds.isNotEmpty()) {
            throw EntityNotFoundException("Translator", missingTranslatorIds, ErrorCode.TRANSLATOR_NOT_FOUND)
        }

        // Validate illustrators
        val illustrators = illustratorRepository.findAllById(illustratorIds)
        val foundIllustratorIds = illustrators.map { it.id }
        val missingIllustratorIds = illustratorIds.filter { it !in foundIllustratorIds }
        if (missingIllustratorIds.isNotEmpty()) {
            throw EntityNotFoundException("Illustrator", missingIllustratorIds, ErrorCode.ILLUSTRATOR_NOT_FOUND)
        }

        val newBook = book.copy(authors = authors, translators = translators, illustrators = illustrators)
        return bookRepository.save(newBook)
    }

    fun getBookById(id: String): Book {
        return bookRepository.findById(id).orElseThrow { 
            EntityNotFoundException("Book", listOf(id), ErrorCode.BOOK_NOT_FOUND)
        }
    }

    fun updateBook(id: String, book: Book, authorIds: List<String>, translatorIds: List<String>, illustratorIds: List<String>): Book {
        val existingBook = getBookById(id)
        
        // Validate authors
        val authors = authorRepository.findAllById(authorIds)
        val foundAuthorIds = authors.map { it.id }
        val missingAuthorIds = authorIds.filter { it !in foundAuthorIds }
        if (missingAuthorIds.isNotEmpty()) {
            throw EntityNotFoundException("Author", missingAuthorIds, ErrorCode.AUTHOR_NOT_FOUND)
        }

        // Validate translators
        val translators = translatorRepository.findAllById(translatorIds)
        val foundTranslatorIds = translators.map { it.id }
        val missingTranslatorIds = translatorIds.filter { it !in foundTranslatorIds }
        if (missingTranslatorIds.isNotEmpty()) {
            throw EntityNotFoundException("Translator", missingTranslatorIds, ErrorCode.TRANSLATOR_NOT_FOUND)
        }

        // Validate illustrators
        val illustrators = illustratorRepository.findAllById(illustratorIds)
        val foundIllustratorIds = illustrators.map { it.id }
        val missingIllustratorIds = illustratorIds.filter { it !in foundIllustratorIds }
        if (missingIllustratorIds.isNotEmpty()) {
            throw EntityNotFoundException("Illustrator", missingIllustratorIds, ErrorCode.ILLUSTRATOR_NOT_FOUND)
        }

        val updatedBook = existingBook.copy(
            title = book.title,
            isbn = book.isbn,
            publisher = book.publisher,
            quizPrice = book.quizPrice,
            thumbnail = book.thumbnail,
            authors = authors,
            translators = translators,
            illustrators = illustrators
        )
        return bookRepository.save(updatedBook)
    }

    fun deleteBook(id: String) {
        bookRepository.deleteById(id)
    }
}
