package kr.co.bookquiz.api.controller

import jakarta.validation.Valid
import kr.co.bookquiz.api.api.response.ApiResponse
import kr.co.bookquiz.api.dto.book.BookCreateRequest
import kr.co.bookquiz.api.dto.book.BookResponse
import kr.co.bookquiz.api.dto.book.BookUpdateRequest
import kr.co.bookquiz.api.service.BookService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/books")
class BookController(private val bookService: BookService) {

    @PostMapping
    fun createBook(
            @Valid @RequestBody bookCreateRequest: BookCreateRequest
    ): ResponseEntity<ApiResponse<BookResponse>> {
        val createdBook = bookService.createBook(bookCreateRequest)
        return ResponseEntity.ok(ApiResponse(true, "Book created successfully", createdBook))
    }

    @PostMapping("/bulk")
    fun createBooksBulk(
            @Valid @RequestBody bookCreateRequests: List<BookCreateRequest>
    ): ResponseEntity<ApiResponse<List<BookResponse>>> {
        val created = bookService.createBooksBulk(bookCreateRequests)
        return ResponseEntity.ok(ApiResponse(true, "Books created successfully", created))
    }

    @GetMapping("/{id}")
    fun getBook(@PathVariable id: Long): ResponseEntity<ApiResponse<BookResponse>> {
        val book = bookService.getBookById(id)
        return ResponseEntity.ok(ApiResponse(true, "Book retrieved successfully", book))
    }

    @PutMapping("/{id}")
    fun updateBook(
            @PathVariable id: Long,
            @Valid @RequestBody bookUpdateRequest: BookUpdateRequest
    ): ResponseEntity<ApiResponse<BookResponse>> {
        val updatedBook = bookService.updateBook(id, bookUpdateRequest)
        return ResponseEntity.ok(ApiResponse(true, "Book updated successfully", updatedBook))
    }

    @DeleteMapping("/{id}")
    fun deleteBook(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> {
        bookService.deleteBook(id)
        return ResponseEntity.ok(ApiResponse(true, "Book deleted successfully", null))
    }
}
