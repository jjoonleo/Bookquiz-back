package kr.co.bookquiz.api.service

import kr.co.bookquiz.api.api.exception.EntityNotFoundException
import kr.co.bookquiz.api.api.exception.ErrorCode
import kr.co.bookquiz.api.dto.book.BookCreateRequest
import kr.co.bookquiz.api.dto.book.BookResponse
import kr.co.bookquiz.api.dto.book.BookUpdateRequest
import kr.co.bookquiz.api.dto.book.toEntity
import kr.co.bookquiz.api.dto.book.toResponse
import kr.co.bookquiz.api.entity.Book
import kr.co.bookquiz.api.repository.BookRepository
import org.springframework.stereotype.Service

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val personService: PersonService
) {

    fun getAllBooks(): List<BookResponse> {
        val books = bookRepository.findAll()
        return books.map { it.toResponse() }
    }

    fun createBook(bookCreateRequest: BookCreateRequest): BookResponse {
        // Find or create authors, translators, and illustrators
        val authors = personService.findOrCreatePersons(bookCreateRequest.authorNames)
        val translators = personService.findOrCreatePersons(bookCreateRequest.translatorNames)
        val illustrators = personService.findOrCreatePersons(bookCreateRequest.illustratorNames)

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

        // Find or create new authors, translators, and illustrators
        val authors = personService.findOrCreatePersons(bookUpdateRequest.authorNames)
        val translators = personService.findOrCreatePersons(bookUpdateRequest.translatorNames)
        val illustrators = personService.findOrCreatePersons(bookUpdateRequest.illustratorNames)

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
        personService.cleanupUnusedPersons(oldAuthors, authors)
        personService.cleanupUnusedPersons(oldTranslators, translators)
        personService.cleanupUnusedPersons(oldIllustrators, illustrators)

        return savedBook.toResponse()
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
        personService.cleanupUnusedPersons(oldAuthors, null)
        personService.cleanupUnusedPersons(oldTranslators, null)
        personService.cleanupUnusedPersons(oldIllustrators, null)
    }
}