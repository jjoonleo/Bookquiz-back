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
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(private val bookRepository: BookRepository) {

    @Transactional(readOnly = true)
    fun getAllBooks(): List<BookResponse> {
        val books = bookRepository.findAll()
        return books.map { it.toResponse() }
    }

    @Transactional
    fun createBook(bookCreateRequest: BookCreateRequest): BookResponse {
        val bookEntity = bookCreateRequest.toEntity()
        val savedBook = bookRepository.save(bookEntity)
        return savedBook.toResponse()
    }

    @Transactional
    fun createBooksBulk(bookCreateRequests: List<BookCreateRequest>): List<BookResponse> {
        if (bookCreateRequests.isEmpty()) return emptyList()
        val entities = bookCreateRequests.map { it.toEntity() }
        val saved = bookRepository.saveAll(entities)
        return saved.map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getBookById(id: Long): BookResponse {
        val book = getBookEntityById(id)
        return book.toResponse()
    }

    @Transactional(readOnly = true)
    fun getBookEntityById(id: Long): Book {
        return bookRepository.findById(id).orElseThrow {
            EntityNotFoundException("Book", listOf(id.toString()), ErrorCode.BOOK_NOT_FOUND)
        }
    }

    @Transactional
    fun updateBook(id: Long, bookUpdateRequest: BookUpdateRequest): BookResponse {
        val existingBook = getBookEntityById(id)

        val updatedBook =
                existingBook.copy(
                        title = bookUpdateRequest.title,
                        isbn = bookUpdateRequest.isbn,
                        publisher = bookUpdateRequest.publisher,
                        quizPrice = bookUpdateRequest.quizPrice,
                        thumbnail = bookUpdateRequest.thumbnail,
                        authors = bookUpdateRequest.authors,
                        translators = bookUpdateRequest.translators,
                        illustrators = bookUpdateRequest.illustrators
                )
        val savedBook = bookRepository.save(updatedBook)

        return savedBook.toResponse()
    }

    @Transactional
    fun deleteBook(id: Long) {
        val existingBook = getBookEntityById(id)
        bookRepository.deleteById(id)
    }
}
