package kr.co.bookquiz.api.controller

import kr.co.bookquiz.api.api.response.ApiResponse
import kr.co.bookquiz.api.entity.Book
import kr.co.bookquiz.api.service.BookService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
class BookController(private val bookService: BookService) {

    @PostMapping
    fun createBook(@RequestBody book: Book, @RequestParam authorIds: List<String>, @RequestParam translatorIds: List<String>, @RequestParam illustratorIds: List<String>): ResponseEntity<ApiResponse<Book>> {
        val createdBook = bookService.createBook(book, authorIds, translatorIds, illustratorIds)
        return ResponseEntity.ok(ApiResponse(true, "Book created successfully", createdBook))
    }

    @GetMapping("/{id}")
    fun getBook(@PathVariable id: String): ResponseEntity<ApiResponse<Book>> {
        val book = bookService.getBookById(id)
        return ResponseEntity.ok(ApiResponse(true, "Book retrieved successfully", book))
    }

    @PutMapping("/{id}")
    fun updateBook(@PathVariable id: String, @RequestBody book: Book, @RequestParam authorIds: List<String>, @RequestParam translatorIds: List<String>, @RequestParam illustratorIds: List<String>): ResponseEntity<ApiResponse<Book>> {
        val updatedBook = bookService.updateBook(id, book, authorIds, translatorIds, illustratorIds)
        return ResponseEntity.ok(ApiResponse(true, "Book updated successfully", updatedBook))
    }

    @DeleteMapping("/{id}")
    fun deleteBook(@PathVariable id: String): ResponseEntity<ApiResponse<Unit>> {
        bookService.deleteBook(id)
        return ResponseEntity.ok(ApiResponse(true, "Book deleted successfully", null))
    }
}
