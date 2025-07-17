package kr.co.bookquiz.api.service

import kr.co.bookquiz.api.api.exception.EntityNotFoundException
import kr.co.bookquiz.api.api.exception.ErrorCode
import kr.co.bookquiz.api.dto.book.BookCreateRequest
import kr.co.bookquiz.api.dto.book.BookUpdateRequest
import kr.co.bookquiz.api.entity.Book
import kr.co.bookquiz.api.entity.Person
import kr.co.bookquiz.api.repository.BookRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.ArgumentMatchers.isNull
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class BookServiceTest {

    private lateinit var bookService: BookService
    private lateinit var bookRepository: BookRepository
    private lateinit var personService: PersonService

    private lateinit var testBook: Book
    private lateinit var testBookCreateRequest: BookCreateRequest
    private lateinit var testBookUpdateRequest: BookUpdateRequest
    private lateinit var testAuthor: Person
    private lateinit var testTranslator: Person
    private lateinit var testIllustrator: Person

    @BeforeEach
    fun setUp() {
        bookRepository = mock(BookRepository::class.java)
        personService = mock(PersonService::class.java)
        
        // Reset mocks to clean state
        reset(bookRepository, personService)

        bookService = BookService(
            bookRepository,
            personService
        )

        testAuthor = Person(id = 1L, name = "Test Author")
        testTranslator = Person(id = 2L, name = "Test Translator")
        testIllustrator = Person(id = 3L, name = "Test Illustrator")

        testBook = Book(
            id = 1L,
            title = "Test Book",
            isbn = "978-0123456789",
            publisher = "Test Publisher",
            quizPrice = 1000,
            thumbnail = "https://example.com/thumb.jpg",
            authors = listOf(testAuthor),
            translators = listOf(testTranslator),
            illustrators = listOf(testIllustrator)
        )

        testBookCreateRequest = BookCreateRequest(
            title = "Test Book",
            isbn = "978-0123456789",
            publisher = "Test Publisher",
            quizPrice = 1000,
            thumbnail = "https://example.com/thumb.jpg",
            authorNames = listOf("Test Author"),
            translatorNames = listOf("Test Translator"),
            illustratorNames = listOf("Test Illustrator")
        )

        testBookUpdateRequest = BookUpdateRequest(
            title = "Updated Test Book",
            isbn = "978-0123456789",
            publisher = "Test Publisher",
            quizPrice = 1000,
            thumbnail = "https://example.com/thumb.jpg",
            authorNames = listOf("Test Author"),
            translatorNames = listOf("Test Translator"),
            illustratorNames = listOf("Test Illustrator")
        )
    }

    @Test
    fun `should create book successfully`() {
        // Given
        given(personService.findOrCreatePersons(listOf("Test Author"))).willReturn(listOf(testAuthor))
        given(personService.findOrCreatePersons(listOf("Test Translator"))).willReturn(listOf(testTranslator))
        given(personService.findOrCreatePersons(listOf("Test Illustrator"))).willReturn(listOf(testIllustrator))
        given(bookRepository.save(any(Book::class.java))).willReturn(testBook)

        // When
        val result = bookService.createBook(testBookCreateRequest)

        // Then
        assertThat(result.title).isEqualTo("Test Book")
        assertThat(result.isbn).isEqualTo("978-0123456789")
        assertThat(result.authors).hasSize(1)
        assertThat(result.authors[0].name).isEqualTo("Test Author")
        then(personService).should().findOrCreatePersons(listOf("Test Author"))
        then(personService).should().findOrCreatePersons(listOf("Test Translator"))
        then(personService).should().findOrCreatePersons(listOf("Test Illustrator"))
        then(bookRepository).should().save(any(Book::class.java))
    }

    @Test
    fun `should create new person when not found`() {
        // Given
        val newPerson = Person(id = 2L, name = "New Person")
        val createRequestWithNewPerson = testBookCreateRequest.copy(authorNames = listOf("New Person"))
        
        given(personService.findOrCreatePersons(listOf("New Person"))).willReturn(listOf(newPerson))
        given(personService.findOrCreatePersons(listOf("Test Translator"))).willReturn(listOf(testTranslator))
        given(personService.findOrCreatePersons(listOf("Test Illustrator"))).willReturn(listOf(testIllustrator))
        given(bookRepository.save(any(Book::class.java))).willReturn(testBook)

        // When
        bookService.createBook(createRequestWithNewPerson)

        // Then
        then(personService).should().findOrCreatePersons(listOf("New Person"))
        then(personService).should().findOrCreatePersons(listOf("Test Translator"))
        then(personService).should().findOrCreatePersons(listOf("Test Illustrator"))
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
        assertThat(result.authors).hasSize(1)
        assertThat(result.authors[0].name).isEqualTo("Test Author")
        then(bookRepository).should().findById(1L)
    }

    @Test
    fun `should throw exception when book not found`() {
        // Given
        given(bookRepository.findById(1L)).willReturn(Optional.empty())

        // When & Then
        assertThrows<EntityNotFoundException> {
            bookService.getBookById(1L)
        }
    }

    @Test
    fun `should update book successfully`() {
        // Given
        val updatedBookData = testBook.copy(title = "Updated Test Book")
        
        given(bookRepository.findById(1L)).willReturn(Optional.of(testBook))
        given(personService.findOrCreatePersons(listOf("Test Author"))).willReturn(listOf(testAuthor))
        given(personService.findOrCreatePersons(listOf("Test Translator"))).willReturn(listOf(testTranslator))
        given(personService.findOrCreatePersons(listOf("Test Illustrator"))).willReturn(listOf(testIllustrator))
        given(bookRepository.save(any(Book::class.java))).willReturn(updatedBookData)

        // When
        val result = bookService.updateBook(1L, testBookUpdateRequest)

        // Then
        assertThat(result.title).isEqualTo("Updated Test Book")
        then(bookRepository).should().findById(1L)
        then(personService).should().findOrCreatePersons(listOf("Test Author"))
        then(personService).should().findOrCreatePersons(listOf("Test Translator"))
        then(personService).should().findOrCreatePersons(listOf("Test Illustrator"))
        then(personService).should(times(3)).cleanupUnusedPersons(anyList(), anyList())
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
        then(personService).should(times(3)).cleanupUnusedPersons(anyList(), isNull())
    }
}