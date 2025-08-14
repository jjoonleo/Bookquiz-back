package kr.co.bookquiz.api.service

import java.util.Optional
import kr.co.bookquiz.api.api.exception.EntityNotFoundException
import kr.co.bookquiz.api.dto.book.BookCreateRequest
import kr.co.bookquiz.api.dto.book.BookUpdateRequest
import kr.co.bookquiz.api.entity.Book
import kr.co.bookquiz.api.repository.BookRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class BookServiceTest {

        private lateinit var bookService: BookService
        private lateinit var bookRepository: BookRepository

        private lateinit var testBook: Book
        private lateinit var testBookCreateRequest: BookCreateRequest
        private lateinit var testBookUpdateRequest: BookUpdateRequest
        private val testAuthors: String = "Test Author"
        private val testTranslators: String = "Test Translator"
        private val testIllustrators: String = "Test Illustrator"

        @BeforeEach
        fun setUp() {
                bookRepository = mock(BookRepository::class.java)
                // Reset mocks to clean state
                reset(bookRepository)

                bookService = BookService(bookRepository)

                testBook =
                        Book(
                                id = 1L,
                                title = "Test Book",
                                isbn = "978-0123456789",
                                publisher = "Test Publisher",
                                quizPrice = 1000,
                                thumbnail = "https://example.com/thumb.jpg",
                                maxAttempt = 3,
                                authors = testAuthors,
                                translators = testTranslators,
                                illustrators = testIllustrators
                        )

                testBookCreateRequest =
                        BookCreateRequest(
                                title = "Test Book",
                                isbn = "978-0123456789",
                                publisher = "Test Publisher",
                                quizPrice = 1000,
                                thumbnail = "https://example.com/thumb.jpg",
                                maxAttempt = 3,
                                authors = testAuthors,
                                translators = testTranslators,
                                illustrators = testIllustrators
                        )

                testBookUpdateRequest =
                        BookUpdateRequest(
                                title = "Updated Test Book",
                                isbn = "978-0123456789",
                                publisher = "Test Publisher",
                                quizPrice = 1000,
                                thumbnail = "https://example.com/thumb.jpg",
                                maxAttempt = 3,
                                authors = testAuthors,
                                translators = testTranslators,
                                illustrators = testIllustrators
                        )
        }

        @Test
        fun `should create book successfully`() {
                // Given
                given(bookRepository.save(any(Book::class.java))).willReturn(testBook)

                // When
                val result = bookService.createBook(testBookCreateRequest)

                // Then
                assertThat(result.title).isEqualTo("Test Book")
                assertThat(result.isbn).isEqualTo("978-0123456789")
                assertThat(result.authors).isEqualTo(testAuthors)
                then(bookRepository).should().save(any(Book::class.java))
        }

        @Test
        fun `should create new person when not found`() {
                // Given
                val createRequestWithNewPerson = testBookCreateRequest.copy(authors = "New Person")
                given(bookRepository.save(any(Book::class.java))).willReturn(testBook)

                // When
                bookService.createBook(createRequestWithNewPerson)

                // Then
                then(bookRepository).should().save(any(Book::class.java))
        }

        @Test
        fun `should get book by id successfully`() {
                // Given
                given(bookRepository.findById(1L)).willReturn(Optional.of(testBook))

                // When
                val result = bookService.getBookById(1L)

                // Then
                assertThat(result.title).isEqualTo("Test Book")
                assertThat(result.authors).isEqualTo(testAuthors)
                then(bookRepository).should().findById(1L)
        }

        @Test
        fun `should throw exception when book not found`() {
                // Given
                given(bookRepository.findById(1L)).willReturn(Optional.empty())

                // When & Then
                assertThrows<EntityNotFoundException> { bookService.getBookById(1L) }
        }

        @Test
        fun `should update book successfully`() {
                // Given
                val updatedBookData = testBook.copy(title = "Updated Test Book")

                given(bookRepository.findById(1L)).willReturn(Optional.of(testBook))
                given(bookRepository.save(any(Book::class.java))).willReturn(updatedBookData)

                // When
                val result = bookService.updateBook(1L, testBookUpdateRequest)

                // Then
                assertThat(result.title).isEqualTo("Updated Test Book")
                then(bookRepository).should().findById(1L)
                then(bookRepository).should().save(any(Book::class.java))
        }

        @Test
        fun `should delete book successfully`() {
                // Given
                given(bookRepository.findById(1L)).willReturn(Optional.of(testBook))

                // When
                bookService.deleteBook(1L)

                // Then
                then(bookRepository).should().findById(1L)
                then(bookRepository).should().deleteById(1L)
        }
}
