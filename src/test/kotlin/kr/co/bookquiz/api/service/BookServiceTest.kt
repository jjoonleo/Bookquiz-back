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
            authorIds = listOf(1L),
            translatorIds = listOf(2L),
            illustratorIds = listOf(3L)
        )

        testBookUpdateRequest = BookUpdateRequest(
            title = "Updated Test Book",
            isbn = "978-0123456789",
            publisher = "Test Publisher",
            quizPrice = 1000,
            thumbnail = "https://example.com/thumbnail.jpg",
            authorIds = listOf(1L),
            translatorIds = listOf(2L),
            illustratorIds = listOf(3L)
        )
    }

    @Test
    fun `should create book successfully`() {
        // Given
        given(authorRepository.findAllById(testBookCreateRequest.authorIds)).willReturn(listOf(testAuthor))
        given(translatorRepository.findAllById(testBookCreateRequest.translatorIds)).willReturn(listOf(testTranslator))
        given(illustratorRepository.findAllById(testBookCreateRequest.illustratorIds)).willReturn(listOf(testIllustrator))
        given(bookRepository.save(any(Book::class.java))).willReturn(testBook)

        // When
        val result = bookService.createBook(testBookCreateRequest)

        // Then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.title).isEqualTo("Test Book")
        assertThat(result.isbn).isEqualTo("978-0123456789")
        assertThat(result.authors).hasSize(1)
        assertThat(result.authors[0].name).isEqualTo("Test Author")
        then(authorRepository).should().findAllById(testBookCreateRequest.authorIds)
        then(translatorRepository).should().findAllById(testBookCreateRequest.translatorIds)
        then(illustratorRepository).should().findAllById(testBookCreateRequest.illustratorIds)
        then(bookRepository).should().save(any(Book::class.java))
    }

    @Test
    fun `should throw exception when author not found during creation`() {
        // Given
        val createRequestWithMissingAuthor = testBookCreateRequest.copy(authorIds = listOf(1L, 2L))

        given(authorRepository.findAllById(createRequestWithMissingAuthor.authorIds)).willReturn(listOf(testAuthor)) // Missing author-2

        // When & Then
        val exception = assertThrows<EntityNotFoundException> {
            bookService.createBook(createRequestWithMissingAuthor)
        }

        assertThat(exception.errorCode).isEqualTo(ErrorCode.AUTHOR_NOT_FOUND)
        assertThat(exception.entityType).isEqualTo("Author")
        assertThat(exception.missingIds).containsExactly("2")
        then(authorRepository).should().findAllById(createRequestWithMissingAuthor.authorIds)
        verifyNoInteractions(bookRepository)
    }

    @Test
    fun `should throw exception when translator not found during creation`() {
        // Given
        val createRequestWithMissingTranslator = testBookCreateRequest.copy(translatorIds = listOf(2L, 3L))

        given(authorRepository.findAllById(createRequestWithMissingTranslator.authorIds)).willReturn(listOf(testAuthor))
        given(translatorRepository.findAllById(createRequestWithMissingTranslator.translatorIds)).willReturn(listOf(testTranslator)) // Missing translator-2

        // When & Then
        val exception = assertThrows<EntityNotFoundException> {
            bookService.createBook(createRequestWithMissingTranslator)
        }

        assertThat(exception.errorCode).isEqualTo(ErrorCode.TRANSLATOR_NOT_FOUND)
        assertThat(exception.entityType).isEqualTo("Translator")
        assertThat(exception.missingIds).containsExactly("3")
        then(authorRepository).should().findAllById(createRequestWithMissingTranslator.authorIds)
        then(translatorRepository).should().findAllById(createRequestWithMissingTranslator.translatorIds)
        verifyNoInteractions(bookRepository)
    }

    @Test
    fun `should throw exception when illustrator not found during creation`() {
        // Given
        val createRequestWithMissingIllustrator = testBookCreateRequest.copy(illustratorIds = listOf(3L, 4L))

        given(authorRepository.findAllById(createRequestWithMissingIllustrator.authorIds)).willReturn(listOf(testAuthor))
        given(translatorRepository.findAllById(createRequestWithMissingIllustrator.translatorIds)).willReturn(listOf(testTranslator))
        given(illustratorRepository.findAllById(createRequestWithMissingIllustrator.illustratorIds)).willReturn(listOf(testIllustrator)) // Missing illustrator-2

        // When & Then
        val exception = assertThrows<EntityNotFoundException> {
            bookService.createBook(createRequestWithMissingIllustrator)
        }

        assertThat(exception.errorCode).isEqualTo(ErrorCode.ILLUSTRATOR_NOT_FOUND)
        assertThat(exception.entityType).isEqualTo("Illustrator")
        assertThat(exception.missingIds).containsExactly("4")
        then(authorRepository).should().findAllById(createRequestWithMissingIllustrator.authorIds)
        then(translatorRepository).should().findAllById(createRequestWithMissingIllustrator.translatorIds)
        then(illustratorRepository).should().findAllById(createRequestWithMissingIllustrator.illustratorIds)
        verifyNoInteractions(bookRepository)
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
        given(authorRepository.findAllById(testBookUpdateRequest.authorIds)).willReturn(listOf(testAuthor))
        given(translatorRepository.findAllById(testBookUpdateRequest.translatorIds)).willReturn(listOf(testTranslator))
        given(illustratorRepository.findAllById(testBookUpdateRequest.illustratorIds)).willReturn(listOf(testIllustrator))
        given(bookRepository.save(any(Book::class.java))).willReturn(updatedBookData)

        // When
        val result = bookService.updateBook(1L, testBookUpdateRequest)

        // Then
        assertThat(result.title).isEqualTo("Updated Test Book")
        assertThat(result.quizPrice).isEqualTo(1000)
        then(bookRepository).should().findById(1L)
        then(authorRepository).should().findAllById(testBookUpdateRequest.authorIds)
        then(translatorRepository).should().findAllById(testBookUpdateRequest.translatorIds)
        then(illustratorRepository).should().findAllById(testBookUpdateRequest.illustratorIds)
        then(bookRepository).should().save(any(Book::class.java))
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
        // When
        bookService.deleteBook(1L)

        // Then
        then(bookRepository).should().deleteById(1L)
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
            authorIds = emptyList(),
            translatorIds = emptyList(),
            illustratorIds = emptyList()
        )

        given(authorRepository.findAllById(emptyList())).willReturn(emptyList())
        given(translatorRepository.findAllById(emptyList())).willReturn(emptyList())
        given(illustratorRepository.findAllById(emptyList())).willReturn(emptyList())
        given(bookRepository.save(any(Book::class.java))).willReturn(bookWithoutRelations)

        // When
        val result = bookService.createBook(createRequestWithoutRelations)

        // Then
        assertThat(result.authors).isEmpty()
        assertThat(result.translators).isEmpty()
        assertThat(result.illustrators).isEmpty()
        then(authorRepository).should().findAllById(emptyList())
        then(translatorRepository).should().findAllById(emptyList())
        then(illustratorRepository).should().findAllById(emptyList())
        then(bookRepository).should().save(any(Book::class.java))
    }

    @Test
    fun `should handle multiple missing entities of same type`() {
        // Given
        val createRequestWithMultipleAuthors = testBookCreateRequest.copy(
            authorIds = listOf(1L, 2L, 3L)
        )
        given(authorRepository.findAllById(createRequestWithMultipleAuthors.authorIds)).willReturn(listOf(testAuthor)) // Only author-1 found

        // When & Then
        val exception = assertThrows<EntityNotFoundException> {
            bookService.createBook(createRequestWithMultipleAuthors)
        }

        assertThat(exception.errorCode).isEqualTo(ErrorCode.AUTHOR_NOT_FOUND)
        assertThat(exception.entityType).isEqualTo("Author")
        assertThat(exception.missingIds).containsExactlyInAnyOrder("2", "3")
    }
}