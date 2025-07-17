package kr.co.bookquiz.api.service

import kr.co.bookquiz.api.api.exception.EntityNotFoundException
import kr.co.bookquiz.api.api.exception.ErrorCode
import kr.co.bookquiz.api.dto.book.BookCreateRequest
import kr.co.bookquiz.api.dto.book.BookResponse
import kr.co.bookquiz.api.dto.book.BookUpdateRequest
import kr.co.bookquiz.api.dto.book.toEntity
import kr.co.bookquiz.api.dto.book.toResponse
import kr.co.bookquiz.api.entity.Book
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
        // Validate authors
        val authors = authorRepository.findAllById(bookCreateRequest.authorIds)
        val foundAuthorIds = authors.map { it.id }
        val missingAuthorIds = bookCreateRequest.authorIds.filter { it !in foundAuthorIds }
        if (missingAuthorIds.isNotEmpty()) {
            throw EntityNotFoundException("Author", missingAuthorIds.map { it.toString() }, ErrorCode.AUTHOR_NOT_FOUND)
        }

        // Validate translators
        val translators = translatorRepository.findAllById(bookCreateRequest.translatorIds)
        val foundTranslatorIds = translators.map { it.id }
        val missingTranslatorIds = bookCreateRequest.translatorIds.filter { it !in foundTranslatorIds }
        if (missingTranslatorIds.isNotEmpty()) {
            throw EntityNotFoundException("Translator", missingTranslatorIds.map { it.toString() }, ErrorCode.TRANSLATOR_NOT_FOUND)
        }

        // Validate illustrators
        val illustrators = illustratorRepository.findAllById(bookCreateRequest.illustratorIds)
        val foundIllustratorIds = illustrators.map { it.id }
        val missingIllustratorIds = bookCreateRequest.illustratorIds.filter { it !in foundIllustratorIds }
        if (missingIllustratorIds.isNotEmpty()) {
            throw EntityNotFoundException("Illustrator", missingIllustratorIds.map { it.toString() }, ErrorCode.ILLUSTRATOR_NOT_FOUND)
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

        // Validate authors
        val authors = authorRepository.findAllById(bookUpdateRequest.authorIds)
        val foundAuthorIds = authors.map { it.id }
        val missingAuthorIds = bookUpdateRequest.authorIds.filter { it !in foundAuthorIds }
        if (missingAuthorIds.isNotEmpty()) {
            throw EntityNotFoundException("Author", missingAuthorIds.map { it.toString() }, ErrorCode.AUTHOR_NOT_FOUND)
        }

        // Validate translators
        val translators = translatorRepository.findAllById(bookUpdateRequest.translatorIds)
        val foundTranslatorIds = translators.map { it.id }
        val missingTranslatorIds = bookUpdateRequest.translatorIds.filter { it !in foundTranslatorIds }
        if (missingTranslatorIds.isNotEmpty()) {
            throw EntityNotFoundException("Translator", missingTranslatorIds.map { it.toString() }, ErrorCode.TRANSLATOR_NOT_FOUND)
        }

        // Validate illustrators
        val illustrators = illustratorRepository.findAllById(bookUpdateRequest.illustratorIds)
        val foundIllustratorIds = illustrators.map { it.id }
        val missingIllustratorIds = bookUpdateRequest.illustratorIds.filter { it !in foundIllustratorIds }
        if (missingIllustratorIds.isNotEmpty()) {
            throw EntityNotFoundException("Illustrator", missingIllustratorIds.map { it.toString() }, ErrorCode.ILLUSTRATOR_NOT_FOUND)
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
        return savedBook.toResponse()
    }

    fun deleteBook(id: Long) {
        bookRepository.deleteById(id)
    }
}