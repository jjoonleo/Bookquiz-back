package kr.co.bookquiz.api.service

import kr.co.bookquiz.api.api.exception.EntityNotFoundException
import kr.co.bookquiz.api.api.exception.ErrorCode
import kr.co.bookquiz.api.dto.book.BookCreateRequest
import kr.co.bookquiz.api.dto.book.BookUpdateRequest
import kr.co.bookquiz.api.entity.Author
import kr.co.bookquiz.api.entity.Book
import kr.co.bookquiz.api.entity.Illustrator
import kr.co.bookquiz.api.entity.Translator
import kr.co.bookquiz.api.repository.AuthorRepository
import kr.co.bookquiz.api.repository.BookRepository
import kr.co.bookquiz.api.repository.IllustratorRepository
import kr.co.bookquiz.api.repository.TranslatorRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class BookServiceTest {

    private lateinit var bookService: BookService
    private lateinit var bookRepository: BookRepository
    private lateinit var authorRepository: AuthorRepository
    private lateinit var translatorRepository: TranslatorRepository
    private lateinit var illustratorRepository: IllustratorRepository

    private lateinit var testBook: Book
    private lateinit var testBookCreateRequest: BookCreateRequest
    private lateinit var testBookUpdateRequest: BookUpdateRequest
    private lateinit var testAuthor: Author
    private lateinit var testTranslator: Translator
    private lateinit var testIllustrator: Illustrator

    @BeforeEach
    fun setUp() {
        bookRepository = mock(BookRepository::class.java)
        authorRepository = mock(AuthorRepository::class.java)
        translatorRepository = mock(TranslatorRepository::class.java)
        illustratorRepository = mock(IllustratorRepository::class.java)

        bookService = BookService(
            bookRepository,
            authorRepository,
            translatorRepository,
            illustratorRepository
        )

        testAuthor = Author(id = 1L, name = "Test Author")
        testTranslator = Translator(id = 2L, name = "Test Translator")
        testIllustrator = Illustrator(id = 3L, name = "Test Illustrator")

        testBook = Book(
            id = 1L,
            title = "Test Book",
            isbn = "978-0123456789",
            publisher = "Test Publisher",
            quizPrice = 1000,
            thumbnail = "https://example.com/thumbnail.jpg",
            authors = listOf(testAuthor),
            translators = listOf(testTranslator),
            illustrators = listOf(testIllustrator)
        )

        testBookCreateRequest = BookCreateRequest(
            title = "Test Book",
            isbn = "978-0123456789",
            publisher = "Test Publisher",
            quizPrice = 1000,
            thumbnail = "https://example.com/thumbnail.jpg",
            authorNames = listOf("Test Author"),
            translatorNames = listOf("Test Translator"),
            illustratorNames = listOf("Test Illustrator")
        )

        testBookUpdateRequest = BookUpdateRequest(
            title = "Updated Test Book",
            isbn = "978-0123456789",
            publisher = "Test Publisher",
            quizPrice = 1000,
            thumbnail = "https://example.com/thumbnail.jpg",
            authorNames = listOf("Test Author"),
            translatorNames = listOf("Test Translator"),
            illustratorNames = listOf("Test Illustrator")
        )
    }

    @Test
    fun `should create book successfully`() {
        // Given
        given(authorRepository.findByName("Test Author")).willReturn(Optional.of(testAuthor))
        given(translatorRepository.findByName("Test Translator")).willReturn(Optional.of(testTranslator))
        given(illustratorRepository.findByName("Test Illustrator")).willReturn(Optional.of(testIllustrator))
        given(bookRepository.save(any(Book::class.java))).willReturn(testBook)

        // When
        val result = bookService.createBook(testBookCreateRequest)

        // Then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.title).isEqualTo("Test Book")
        assertThat(result.isbn).isEqualTo("978-0123456789")
        assertThat(result.authors).hasSize(1)
        assertThat(result.authors[0].name).isEqualTo("Test Author")
        then(authorRepository).should().findByName("Test Author")
        then(translatorRepository).should().findByName("Test Translator")
        then(illustratorRepository).should().findByName("Test Illustrator")
        then(bookRepository).should().save(any(Book::class.java))
    }

    @Test
    fun `should create new author when not found`() {
        // Given
        val newAuthor = Author(id = 2L, name = "New Author")
        val createRequestWithNewAuthor = testBookCreateRequest.copy(authorNames = listOf("New Author"))
        
        given(authorRepository.findByName("New Author")).willReturn(Optional.empty())
        given(authorRepository.save(any(Author::class.java))).willReturn(newAuthor)
        given(translatorRepository.findByName("Test Translator")).willReturn(Optional.of(testTranslator))
        given(illustratorRepository.findByName("Test Illustrator")).willReturn(Optional.of(testIllustrator))
        given(bookRepository.save(any(Book::class.java))).willReturn(testBook)

        // When
        bookService.createBook(createRequestWithNewAuthor)

        // Then
        then(authorRepository).should().findByName("New Author")
        then(authorRepository).should().save(any(Author::class.java))
        then(bookRepository).should().save(any(Book::class.java))
    }

    @Test
    fun `should create new translator when not found`() {
        // Given
        val newTranslator = Translator(id = 3L, name = "New Translator")
        val createRequestWithNewTranslator = testBookCreateRequest.copy(translatorNames = listOf("New Translator"))
        
        given(authorRepository.findByName("Test Author")).willReturn(Optional.of(testAuthor))
        given(translatorRepository.findByName("New Translator")).willReturn(Optional.empty())
        given(translatorRepository.save(any(Translator::class.java))).willReturn(newTranslator)
        given(illustratorRepository.findByName("Test Illustrator")).willReturn(Optional.of(testIllustrator))
        given(bookRepository.save(any(Book::class.java))).willReturn(testBook)

        // When
        bookService.createBook(createRequestWithNewTranslator)

        // Then
        then(translatorRepository).should().findByName("New Translator")
        then(translatorRepository).should().save(any(Translator::class.java))
        then(bookRepository).should().save(any(Book::class.java))
    }

    @Test
    fun `should create new illustrator when not found`() {
        // Given
        val newIllustrator = Illustrator(id = 4L, name = "New Illustrator")
        val createRequestWithNewIllustrator = testBookCreateRequest.copy(illustratorNames = listOf("New Illustrator"))
        
        given(authorRepository.findByName("Test Author")).willReturn(Optional.of(testAuthor))
        given(translatorRepository.findByName("Test Translator")).willReturn(Optional.of(testTranslator))
        given(illustratorRepository.findByName("New Illustrator")).willReturn(Optional.empty())
        given(illustratorRepository.save(any(Illustrator::class.java))).willReturn(newIllustrator)
        given(bookRepository.save(any(Book::class.java))).willReturn(testBook)

        // When
        bookService.createBook(createRequestWithNewIllustrator)

        // Then
        then(illustratorRepository).should().findByName("New Illustrator")
        then(illustratorRepository).should().save(any(Illustrator::class.java))
        then(bookRepository).should().save(any(Book::class.java))
    }

    @Test
    fun `should get book by id successfully`() {
        // Given
        given(bookRepository.findById(1L)).willReturn(Optional.of(testBook))

        // When
        val result = bookService.getBookById(1L)

        // Then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.title).isEqualTo("Test Book")
        assertThat(result.authors).hasSize(1)
        assertThat(result.authors[0].name).isEqualTo("Test Author")
        then(bookRepository).should().findById(1L)
    }

    @Test
    fun `should throw exception when book not found by id`() {
        // Given
        given(bookRepository.findById(999L)).willReturn(Optional.empty())

        // When & Then
        val exception = assertThrows<EntityNotFoundException> {
            bookService.getBookById(999L)
        }

        assertThat(exception.errorCode).isEqualTo(ErrorCode.BOOK_NOT_FOUND)
        assertThat(exception.entityType).isEqualTo("Book")
        assertThat(exception.missingIds).containsExactly("999")
        then(bookRepository).should().findById(999L)
    }

    @Test
    fun `should update book successfully`() {
        // Given
        val updatedBookData = testBook.copy(
            title = "Updated Test Book",
            quizPrice = 1000
        )

        given(bookRepository.findById(1L)).willReturn(Optional.of(testBook))
        given(authorRepository.findByName("Test Author")).willReturn(Optional.of(testAuthor))
        given(translatorRepository.findByName("Test Translator")).willReturn(Optional.of(testTranslator))
        given(illustratorRepository.findByName("Test Illustrator")).willReturn(Optional.of(testIllustrator))
        given(bookRepository.save(any(Book::class.java))).willReturn(updatedBookData)

        // When
        val result = bookService.updateBook(1L, testBookUpdateRequest)

        // Then
        assertThat(result.title).isEqualTo("Updated Test Book")
        assertThat(result.quizPrice).isEqualTo(1000)
        then(bookRepository).should().findById(1L)
        then(authorRepository).should().findByName("Test Author")
        then(translatorRepository).should().findByName("Test Translator")
        then(illustratorRepository).should().findByName("Test Illustrator")
        then(bookRepository).should().save(any(Book::class.java))
    }

    @Test
    fun `should delete unused author when updating book`() {
        // Given
        val oldAuthor = Author(id = 2L, name = "Old Author")
        val bookWithOldAuthor = testBook.copy(authors = listOf(oldAuthor))
        val updatedBookData = testBook.copy(
            title = "Updated Test Book",
            authors = listOf(testAuthor)
        )

        given(bookRepository.findById(1L)).willReturn(Optional.of(bookWithOldAuthor))
        given(authorRepository.findByName("Test Author")).willReturn(Optional.of(testAuthor))
        given(translatorRepository.findByName("Test Translator")).willReturn(Optional.of(testTranslator))
        given(illustratorRepository.findByName("Test Illustrator")).willReturn(Optional.of(testIllustrator))
        given(authorRepository.countBooksByAuthorId(2L)).willReturn(0L) // No books left
        given(bookRepository.save(any(Book::class.java))).willReturn(updatedBookData)

        // When
        bookService.updateBook(1L, testBookUpdateRequest)

        // Then
        then(authorRepository).should().countBooksByAuthorId(2L)
        then(authorRepository).should().deleteById(2L)
    }

    @Test
    fun `should not delete author when still used by other books`() {
        // Given
        val oldAuthor = Author(id = 2L, name = "Old Author")
        val bookWithOldAuthor = testBook.copy(authors = listOf(oldAuthor))
        val updatedBookData = testBook.copy(
            title = "Updated Test Book",
            authors = listOf(testAuthor)
        )

        given(bookRepository.findById(1L)).willReturn(Optional.of(bookWithOldAuthor))
        given(authorRepository.findByName("Test Author")).willReturn(Optional.of(testAuthor))
        given(translatorRepository.findByName("Test Translator")).willReturn(Optional.of(testTranslator))
        given(illustratorRepository.findByName("Test Illustrator")).willReturn(Optional.of(testIllustrator))
        given(authorRepository.countBooksByAuthorId(2L)).willReturn(1L) // Still has books
        given(bookRepository.save(any(Book::class.java))).willReturn(updatedBookData)

        // When
        bookService.updateBook(1L, testBookUpdateRequest)

        // Then
        then(authorRepository).should().countBooksByAuthorId(2L)
        then(authorRepository).should(never()).deleteById(2L)
    }

    @Test
    fun `should create new author when updating book`() {
        // Given
        val newAuthor = Author(id = 3L, name = "New Author")
        val updateRequestWithNewAuthor = testBookUpdateRequest.copy(authorNames = listOf("New Author"))
        val updatedBookData = testBook.copy(
            title = "Updated Test Book",
            authors = listOf(newAuthor)
        )

        given(bookRepository.findById(1L)).willReturn(Optional.of(testBook))
        given(authorRepository.findByName("New Author")).willReturn(Optional.empty())
        given(authorRepository.save(any(Author::class.java))).willReturn(newAuthor)
        given(translatorRepository.findByName("Test Translator")).willReturn(Optional.of(testTranslator))
        given(illustratorRepository.findByName("Test Illustrator")).willReturn(Optional.of(testIllustrator))
        given(authorRepository.countBooksByAuthorId(1L)).willReturn(0L) // Old author has no books
        given(bookRepository.save(any(Book::class.java))).willReturn(updatedBookData)

        // When
        bookService.updateBook(1L, updateRequestWithNewAuthor)

        // Then
        then(authorRepository).should().findByName("New Author")
        then(authorRepository).should().save(any(Author::class.java))
        then(authorRepository).should().countBooksByAuthorId(1L)
        then(authorRepository).should().deleteById(1L)
    }

    @Test
    fun `should throw exception when updating non-existent book`() {
        // Given
        given(bookRepository.findById(999L)).willReturn(Optional.empty())

        // When & Then
        val exception = assertThrows<EntityNotFoundException> {
            bookService.updateBook(999L, testBookUpdateRequest)
        }

        assertThat(exception.errorCode).isEqualTo(ErrorCode.BOOK_NOT_FOUND)
        assertThat(exception.entityType).isEqualTo("Book")
        assertThat(exception.missingIds).containsExactly("999")
        then(bookRepository).should().findById(999L)
    }

    @Test
    fun `should delete book successfully`() {
        // Given
        given(bookRepository.findById(1L)).willReturn(Optional.of(testBook))
        given(authorRepository.countBooksByAuthorId(1L)).willReturn(0L) // No other books use this author
        given(translatorRepository.countBooksByTranslatorId(2L)).willReturn(0L) // No other books use this translator
        given(illustratorRepository.countBooksByIllustratorId(3L)).willReturn(0L) // No other books use this illustrator

        // When
        bookService.deleteBook(1L)

        // Then
        then(bookRepository).should().findById(1L)
        then(bookRepository).should().deleteById(1L)
        then(authorRepository).should().countBooksByAuthorId(1L)
        then(authorRepository).should().deleteById(1L)
        then(translatorRepository).should().countBooksByTranslatorId(2L)
        then(translatorRepository).should().deleteById(2L)
        then(illustratorRepository).should().countBooksByIllustratorId(3L)
        then(illustratorRepository).should().deleteById(3L)
    }

    @Test
    fun `should not delete entities when still used by other books during deletion`() {
        // Given
        given(bookRepository.findById(1L)).willReturn(Optional.of(testBook))
        given(authorRepository.countBooksByAuthorId(1L)).willReturn(1L) // Still used by other books
        given(translatorRepository.countBooksByTranslatorId(2L)).willReturn(1L) // Still used by other books
        given(illustratorRepository.countBooksByIllustratorId(3L)).willReturn(1L) // Still used by other books

        // When
        bookService.deleteBook(1L)

        // Then
        then(bookRepository).should().findById(1L)
        then(bookRepository).should().deleteById(1L)
        then(authorRepository).should().countBooksByAuthorId(1L)
        then(authorRepository).should(never()).deleteById(1L)
        then(translatorRepository).should().countBooksByTranslatorId(2L)
        then(translatorRepository).should(never()).deleteById(2L)
        then(illustratorRepository).should().countBooksByIllustratorId(3L)
        then(illustratorRepository).should(never()).deleteById(3L)
    }

    @Test
    fun `should create book with empty authors, translators, and illustrators`() {
        // Given
        val bookWithoutRelations = testBook.copy(
            authors = emptyList(),
            translators = emptyList(),
            illustrators = emptyList()
        )

        val createRequestWithoutRelations = testBookCreateRequest.copy(
            authorNames = emptyList(),
            translatorNames = emptyList(),
            illustratorNames = emptyList()
        )

        given(bookRepository.save(any(Book::class.java))).willReturn(bookWithoutRelations)

        // When
        val result = bookService.createBook(createRequestWithoutRelations)

        // Then
        assertThat(result.authors).isEmpty()
        assertThat(result.translators).isEmpty()
        assertThat(result.illustrators).isEmpty()
        then(bookRepository).should().save(any(Book::class.java))
    }

    @Test
    fun `should handle multiple authors with some existing and some new`() {
        // Given
        val existingAuthor = testAuthor
        val newAuthor = Author(id = 2L, name = "New Author")
        val createRequestWithMultipleAuthors = testBookCreateRequest.copy(
            authorNames = listOf("Test Author", "New Author")
        )
        
        given(authorRepository.findByName("Test Author")).willReturn(Optional.of(existingAuthor))
        given(authorRepository.findByName("New Author")).willReturn(Optional.empty())
        given(authorRepository.save(any(Author::class.java))).willReturn(newAuthor)
        given(translatorRepository.findByName("Test Translator")).willReturn(Optional.of(testTranslator))
        given(illustratorRepository.findByName("Test Illustrator")).willReturn(Optional.of(testIllustrator))
        given(bookRepository.save(any(Book::class.java))).willReturn(testBook)

        // When
        bookService.createBook(createRequestWithMultipleAuthors)

        // Then
        then(authorRepository).should().findByName("Test Author")
        then(authorRepository).should().findByName("New Author")
        then(authorRepository).should().save(any(Author::class.java))
        then(bookRepository).should().save(any(Book::class.java))
    }
}