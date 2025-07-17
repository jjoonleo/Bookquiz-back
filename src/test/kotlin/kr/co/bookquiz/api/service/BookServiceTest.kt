package kr.co.bookquiz.api.service

import kr.co.bookquiz.api.api.exception.ApiException
import kr.co.bookquiz.api.api.exception.EntityNotFoundException
import kr.co.bookquiz.api.api.exception.ErrorCode
import kr.co.bookquiz.api.entity.Author
import kr.co.bookquiz.api.entity.Book
import kr.co.bookquiz.api.entity.Illustrator
import kr.co.bookquiz.api.entity.Translator
import kr.co.bookquiz.api.repository.AuthorRepository
import kr.co.bookquiz.api.repository.BookRepository
import kr.co.bookquiz.api.repository.IllustratorRepository
import kr.co.bookquiz.api.repository.TranslatorRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*
import org.assertj.core.api.Assertions.assertThat

@ExtendWith(MockitoExtension::class)
class BookServiceTest {

    private lateinit var bookService: BookService
    private lateinit var bookRepository: BookRepository
    private lateinit var authorRepository: AuthorRepository
    private lateinit var translatorRepository: TranslatorRepository
    private lateinit var illustratorRepository: IllustratorRepository

    private lateinit var testBook: Book
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
        
        given(authorRepository.findAllById(authorIds)).willReturn(listOf(testAuthor))
        given(translatorRepository.findAllById(translatorIds)).willReturn(listOf(testTranslator))
        given(illustratorRepository.findAllById(illustratorIds)).willReturn(listOf(testIllustrator))
        given(bookRepository.save(any(Book::class.java))).willReturn(testBook)

        // When
        val result = bookService.createBook(testBook, authorIds, translatorIds, illustratorIds)

        // Then
        assertThat(result).isEqualTo(testBook)
        then(authorRepository).should().findAllById(authorIds)
        then(translatorRepository).should().findAllById(translatorIds)
        then(illustratorRepository).should().findAllById(illustratorIds)
        then(bookRepository).should().save(any(Book::class.java))
    }

    @Test
    fun `should throw exception when author not found during creation`() {
        // Given
        val authorIds = listOf("author-1", "author-2")
        val translatorIds = listOf("translator-1")
        val illustratorIds = listOf("illustrator-1")
        
        given(authorRepository.findAllById(authorIds)).willReturn(listOf(testAuthor)) // Missing author-2

        // When & Then
        val exception = assertThrows<EntityNotFoundException> {
            bookService.createBook(testBook, authorIds, translatorIds, illustratorIds)
        }

        assertThat(exception.errorCode).isEqualTo(ErrorCode.AUTHOR_NOT_FOUND)
        assertThat(exception.entityType).isEqualTo("Author")
        assertThat(exception.missingIds).containsExactly("author-2")
        then(authorRepository).should().findAllById(authorIds)
        verifyNoInteractions(bookRepository)
    }

    @Test
    fun `should throw exception when translator not found during creation`() {
        // Given
        val authorIds = listOf("author-1")
        val translatorIds = listOf("translator-1", "translator-2")
        val illustratorIds = listOf("illustrator-1")
        
        given(authorRepository.findAllById(authorIds)).willReturn(listOf(testAuthor))
        given(translatorRepository.findAllById(translatorIds)).willReturn(listOf(testTranslator)) // Missing translator-2

        // When & Then
        val exception = assertThrows<EntityNotFoundException> {
            bookService.createBook(testBook, authorIds, translatorIds, illustratorIds)
        }

        assertThat(exception.errorCode).isEqualTo(ErrorCode.TRANSLATOR_NOT_FOUND)
        assertThat(exception.entityType).isEqualTo("Translator")
        assertThat(exception.missingIds).containsExactly("translator-2")
        then(authorRepository).should().findAllById(authorIds)
        then(translatorRepository).should().findAllById(translatorIds)
        verifyNoInteractions(bookRepository)
    }

    @Test
    fun `should throw exception when illustrator not found during creation`() {
        // Given
        val authorIds = listOf("author-1")
        val translatorIds = listOf("translator-1")
        val illustratorIds = listOf("illustrator-1", "illustrator-2")
        
        given(authorRepository.findAllById(authorIds)).willReturn(listOf(testAuthor))
        given(translatorRepository.findAllById(translatorIds)).willReturn(listOf(testTranslator))
        given(illustratorRepository.findAllById(illustratorIds)).willReturn(listOf(testIllustrator)) // Missing illustrator-2

        // When & Then
        val exception = assertThrows<EntityNotFoundException> {
            bookService.createBook(testBook, authorIds, translatorIds, illustratorIds)
        }

        assertThat(exception.errorCode).isEqualTo(ErrorCode.ILLUSTRATOR_NOT_FOUND)
        assertThat(exception.entityType).isEqualTo("Illustrator")
        assertThat(exception.missingIds).containsExactly("illustrator-2")
        then(authorRepository).should().findAllById(authorIds)
        then(translatorRepository).should().findAllById(translatorIds)
        then(illustratorRepository).should().findAllById(illustratorIds)
        verifyNoInteractions(bookRepository)
    }

    @Test
    fun `should get book by id successfully`() {
        // Given
        given(bookRepository.findById("book-1")).willReturn(Optional.of(testBook))

        // When
        val result = bookService.getBookById("book-1")

        // Then
        assertThat(result).isEqualTo(testBook)
        then(bookRepository).should().findById("book-1")
    }

    @Test
    fun `should throw exception when book not found by id`() {
        // Given
        given(bookRepository.findById("nonexistent")).willReturn(Optional.empty())

        // When & Then
        val exception = assertThrows<EntityNotFoundException> {
            bookService.getBookById("nonexistent")
        }

        assertThat(exception.errorCode).isEqualTo(ErrorCode.BOOK_NOT_FOUND)
        assertThat(exception.entityType).isEqualTo("Book")
        assertThat(exception.missingIds).containsExactly("nonexistent")
        then(bookRepository).should().findById("nonexistent")
    }

    @Test
    fun `should update book successfully`() {
        // Given
        val authorIds = listOf("author-1")
        val translatorIds = listOf("translator-1")
        val illustratorIds = listOf("illustrator-1")
        
        val updatedBookData = testBook.copy(
            title = "Updated Book Title",
            quizPrice = 2000
        )
        
        given(bookRepository.findById("book-1")).willReturn(Optional.of(testBook))
        given(authorRepository.findAllById(authorIds)).willReturn(listOf(testAuthor))
        given(translatorRepository.findAllById(translatorIds)).willReturn(listOf(testTranslator))
        given(illustratorRepository.findAllById(illustratorIds)).willReturn(listOf(testIllustrator))
        given(bookRepository.save(any(Book::class.java))).willReturn(updatedBookData)

        // When
        val result = bookService.updateBook("book-1", updatedBookData, authorIds, translatorIds, illustratorIds)

        // Then
        assertThat(result.title).isEqualTo("Updated Book Title")
        assertThat(result.quizPrice).isEqualTo(2000)
        then(bookRepository).should().findById("book-1")
        then(authorRepository).should().findAllById(authorIds)
        then(translatorRepository).should().findAllById(translatorIds)
        then(illustratorRepository).should().findAllById(illustratorIds)
        then(bookRepository).should().save(any(Book::class.java))
    }

    @Test
    fun `should throw exception when updating non-existent book`() {
        // Given
        given(bookRepository.findById("nonexistent")).willReturn(Optional.empty())

        // When & Then
        val exception = assertThrows<EntityNotFoundException> {
            bookService.updateBook("nonexistent", testBook, emptyList(), emptyList(), emptyList())
        }

        assertThat(exception.errorCode).isEqualTo(ErrorCode.BOOK_NOT_FOUND)
        assertThat(exception.entityType).isEqualTo("Book")
        assertThat(exception.missingIds).containsExactly("nonexistent")
        then(bookRepository).should().findById("nonexistent")
    }

    @Test
    fun `should delete book successfully`() {
        // When
        bookService.deleteBook("book-1")

        // Then
        then(bookRepository).should().deleteById("book-1")
    }

    @Test
    fun `should create book with empty authors, translators, and illustrators`() {
        // Given
        val bookWithoutRelations = testBook.copy(
            authors = emptyList(),
            translators = emptyList(),
            illustrators = emptyList()
        )
        
        given(authorRepository.findAllById(emptyList())).willReturn(emptyList())
        given(translatorRepository.findAllById(emptyList())).willReturn(emptyList())
        given(illustratorRepository.findAllById(emptyList())).willReturn(emptyList())
        given(bookRepository.save(any(Book::class.java))).willReturn(bookWithoutRelations)

        // When
        val result = bookService.createBook(bookWithoutRelations, emptyList(), emptyList(), emptyList())

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
        val authorIds = listOf("author-1", "author-2", "author-3")
        given(authorRepository.findAllById(authorIds)).willReturn(listOf(testAuthor)) // Only author-1 found

        // When & Then
        val exception = assertThrows<EntityNotFoundException> {
            bookService.createBook(testBook, authorIds, emptyList(), emptyList())
        }

        assertThat(exception.errorCode).isEqualTo(ErrorCode.AUTHOR_NOT_FOUND)
        assertThat(exception.entityType).isEqualTo("Author")
        assertThat(exception.missingIds).containsExactlyInAnyOrder("author-2", "author-3")
    }
}
