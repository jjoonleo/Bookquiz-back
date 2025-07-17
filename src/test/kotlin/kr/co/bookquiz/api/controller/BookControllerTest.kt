package kr.co.bookquiz.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import kr.co.bookquiz.api.api.exception.EntityNotFoundException
import kr.co.bookquiz.api.api.exception.ErrorCode
import kr.co.bookquiz.api.entity.Author
import kr.co.bookquiz.api.entity.Book
import kr.co.bookquiz.api.entity.Illustrator
import kr.co.bookquiz.api.entity.Translator
import kr.co.bookquiz.api.service.BookService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(BookController::class)
class BookControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var bookService: BookService

    private lateinit var testBook: Book
    private lateinit var testAuthor: Author
    private lateinit var testTranslator: Translator
    private lateinit var testIllustrator: Illustrator

    @BeforeEach
    fun setUp() {
        testAuthor = Author(id = "author-1", name = "Test Author")
        testTranslator = Translator(id = "translator-1", name = "Test Translator")
        testIllustrator = Illustrator(id = "illustrator-1", name = "Test Illustrator")

        testBook = Book(
            id = "book-1",
            title = "Test Book",
            isbn = "978-0123456789",
            publisher = "Test Publisher",
            quizPrice = 1000,
            thumbnail = "https://example.com/thumbnail.jpg",
            authors = listOf(testAuthor),
            translators = listOf(testTranslator),
            illustrators = listOf(testIllustrator)
        )
    }

    @Test
    fun `should create book successfully`() {
        // Given
        val authorIds = listOf("author-1")
        val translatorIds = listOf("translator-1")
        val illustratorIds = listOf("illustrator-1")
        
        given(bookService.createBook(any(Book::class.java), eq(authorIds), eq(translatorIds), eq(illustratorIds)))
            .willReturn(testBook)

        // When & Then
        mockMvc.perform(
            post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBook))
                .param("authorIds", "author-1")
                .param("translatorIds", "translator-1")
                .param("illustratorIds", "illustrator-1")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Book created successfully"))
            .andExpect(jsonPath("$.data.id").value("book-1"))
            .andExpect(jsonPath("$.data.title").value("Test Book"))
            .andExpect(jsonPath("$.data.isbn").value("978-0123456789"))
            .andExpect(jsonPath("$.data.publisher").value("Test Publisher"))
            .andExpect(jsonPath("$.data.quizPrice").value(1000))

        then(bookService).should().createBook(any(Book::class.java), eq(authorIds), eq(translatorIds), eq(illustratorIds))
    }

    @Test
    fun `should get book by id successfully`() {
        // Given
        given(bookService.getBookById("book-1")).willReturn(testBook)

        // When & Then
        mockMvc.perform(get("/books/book-1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Book retrieved successfully"))
            .andExpect(jsonPath("$.data.id").value("book-1"))
            .andExpect(jsonPath("$.data.title").value("Test Book"))

        then(bookService).should().getBookById("book-1")
    }

    @Test
    fun `should return 404 when book not found`() {
        // Given
        given(bookService.getBookById("nonexistent"))
            .willThrow(EntityNotFoundException("Book", listOf("nonexistent"), ErrorCode.BOOK_NOT_FOUND))

        // When & Then
        mockMvc.perform(get("/books/nonexistent"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.title").value("Entity Not Found"))
            .andExpect(jsonPath("$.detail").value("Book not found: nonexistent"))

        then(bookService).should().getBookById("nonexistent")
    }

    @Test
    fun `should update book successfully`() {
        // Given
        val authorIds = listOf("author-1")
        val translatorIds = listOf("translator-1")
        val illustratorIds = listOf("illustrator-1")
        
        val updatedBook = testBook.copy(title = "Updated Test Book")
        
        given(bookService.updateBook(eq("book-1"), any(Book::class.java), eq(authorIds), eq(translatorIds), eq(illustratorIds)))
            .willReturn(updatedBook)

        // When & Then
        mockMvc.perform(
            put("/books/book-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook))
                .param("authorIds", "author-1")
                .param("translatorIds", "translator-1")
                .param("illustratorIds", "illustrator-1")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Book updated successfully"))
            .andExpect(jsonPath("$.data.title").value("Updated Test Book"))

        then(bookService).should().updateBook(eq("book-1"), any(Book::class.java), eq(authorIds), eq(translatorIds), eq(illustratorIds))
    }

    @Test
    fun `should delete book successfully`() {
        // When & Then
        mockMvc.perform(delete("/books/book-1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Book deleted successfully"))

        then(bookService).should().deleteBook("book-1")
    }

    @Test
    fun `should handle validation errors during book creation`() {
        // Given
        val invalidBook = testBook.copy(title = "") // Empty title

        // When & Then
        mockMvc.perform(
            post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBook))
                .param("authorIds", "author-1")
                .param("translatorIds", "translator-1")
                .param("illustratorIds", "illustrator-1")
        )
            .andExpect(status().isInternalServerError) // May vary based on validation configuration

        verifyNoInteractions(bookService)
    }
}
