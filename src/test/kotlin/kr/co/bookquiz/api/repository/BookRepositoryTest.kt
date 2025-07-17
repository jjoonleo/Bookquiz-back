package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.Author
import kr.co.bookquiz.api.entity.Book
import kr.co.bookquiz.api.entity.Illustrator
import kr.co.bookquiz.api.entity.Translator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

import kr.co.bookquiz.api.TestcontainersConfiguration
import org.assertj.core.api.Assertions.assertThat

@DataJpaTest
@Import(TestcontainersConfiguration::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    private lateinit var testBook: Book
    private lateinit var testAuthor: Author
    private lateinit var testTranslator: Translator
    private lateinit var testIllustrator: Illustrator

    @BeforeEach
    fun setUp() {
        // Create entities using repository save methods to avoid persistence issues
        testAuthor = Author(id = "author-1", name = "Test Author")
        testTranslator = Translator(id = "translator-1", name = "Test Translator")
        testIllustrator = Illustrator(id = "illustrator-1", name = "Test Illustrator")

        // Simple approach - just create fresh entities for each test
        // The test framework will handle cleanup automatically

        testBook = Book(
            id = "book-1",
            title = "Test Book",
            isbn = "9780123456789",
            publisher = "Test Publisher",
            quizPrice = 1000,
            thumbnail = "https://example.com/thumbnail.jpg",
            authors = listOf(testAuthor),
            translators = listOf(testTranslator),
            illustrators = listOf(testIllustrator)
        )
    }

    @Test
    fun `should save and find book by id`() {
        // Given - create a simple book without relationships first
        val simpleBook = Book(
            id = "simple-book",
            title = "Simple Test Book",
            isbn = "9780123456789", 
            publisher = "Test Publisher",
            quizPrice = 1000,
            thumbnail = "https://example.com/thumbnail.jpg"
        )

        // When
        val savedBook = bookRepository.save(simpleBook)

        // Then
        val foundBook = bookRepository.findById(savedBook.id)
        assertThat(foundBook).isPresent
        assertThat(foundBook.get().title).isEqualTo("Simple Test Book")
        assertThat(foundBook.get().isbn).isEqualTo("9780123456789")
        assertThat(foundBook.get().publisher).isEqualTo("Test Publisher")
        assertThat(foundBook.get().quizPrice).isEqualTo(1000)
        assertThat(foundBook.get().thumbnail).isEqualTo("https://example.com/thumbnail.jpg")
    }

    @Test
    fun `should save book with authors, translators, and illustrators`() {
        // Given - create entities using repository first
        val author = Author(id = "rel-author", name = "Test Author")
        val translator = Translator(id = "rel-translator", name = "Test Translator")  
        val illustrator = Illustrator(id = "rel-illustrator", name = "Test Illustrator")
        
        // Persist the dependent entities first using TestEntityManager
        testEntityManager.persistAndFlush(author)
        testEntityManager.persistAndFlush(translator)
        testEntityManager.persistAndFlush(illustrator)
        
        val book = Book(
            id = "rel-book",
            title = "Book with Relations",
            isbn = "9780123456790",
            publisher = "Relation Publisher", 
            quizPrice = 2000,
            thumbnail = "https://example.com/relations.jpg",
            authors = listOf(author),
            translators = listOf(translator),
            illustrators = listOf(illustrator)
        )

        // When - save using repository
        val savedBook = bookRepository.save(book)

        // Then
        val foundBook = bookRepository.findById(savedBook.id)
        assertThat(foundBook).isPresent
        
        val retrievedBook = foundBook.get()
        assertThat(retrievedBook.authors).hasSize(1)
        assertThat(retrievedBook.authors.first().name).isEqualTo("Test Author")
        assertThat(retrievedBook.translators).hasSize(1)
        assertThat(retrievedBook.translators.first().name).isEqualTo("Test Translator")
        assertThat(retrievedBook.illustrators).hasSize(1)
        assertThat(retrievedBook.illustrators.first().name).isEqualTo("Test Illustrator")
    }

    @Test
    fun `should update book`() {
        // Given - create and save a simple book
        val book = Book(
            id = "update-book",
            title = "Original Title",
            isbn = "9780123456791",
            publisher = "Update Publisher",
            quizPrice = 1500,
            thumbnail = "https://example.com/original.jpg"
        )
        val savedBook = bookRepository.save(book)

        // When - create updated book instance and save it
        val updatedBook = Book(
            id = savedBook.id,
            title = "Updated Test Book",
            isbn = savedBook.isbn,
            publisher = savedBook.publisher,
            quizPrice = 2000,
            thumbnail = savedBook.thumbnail
        )
        bookRepository.save(updatedBook)

        // Then
        val foundBook = bookRepository.findById(savedBook.id)
        assertThat(foundBook).isPresent
        assertThat(foundBook.get().title).isEqualTo("Updated Test Book")
        assertThat(foundBook.get().quizPrice).isEqualTo(2000)
    }

    @Test
    fun `should delete book`() {
        // Given - create and save a simple book
        val book = Book(
            id = "delete-book",
            title = "Book to Delete",
            isbn = "9780123456792",
            publisher = "Delete Publisher",
            quizPrice = 1000,
            thumbnail = "https://example.com/delete.jpg"
        )
        val savedBook = bookRepository.save(book)

        // When
        bookRepository.deleteById(savedBook.id)

        // Then
        val foundBook = bookRepository.findById(savedBook.id)
        assertThat(foundBook).isNotPresent
    }

    @Test
    fun `should find all books`() {
        // Given - create and save two simple books
        val book1 = Book(
            id = "list-book-1",
            title = "First Book",
            isbn = "9780123456793",
            publisher = "List Publisher",
            quizPrice = 1000,
            thumbnail = "https://example.com/first.jpg"
        )
        val book2 = Book(
            id = "list-book-2",
            title = "Second Book",
            isbn = "9780123456794",
            publisher = "List Publisher",
            quizPrice = 1500,
            thumbnail = "https://example.com/second.jpg"
        )
        
        bookRepository.save(book1)
        bookRepository.save(book2)

        // When
        val allBooks = bookRepository.findAll()

        // Then
        assertThat(allBooks).hasSize(2)
        assertThat(allBooks.map { it.title }).containsExactlyInAnyOrder("First Book", "Second Book")
    }

    @Test
    fun `should save book without optional fields`() {
        // Given - create book without optional fields
        val bookWithoutOptional = Book(
            id = "minimal-book",
            title = "Minimal Book",
            isbn = "9780123456795",
            publisher = "Minimal Publisher",
            quizPrice = 500,
            thumbnail = null
        )

        // When
        val savedBook = bookRepository.save(bookWithoutOptional)

        // Then
        val foundBook = bookRepository.findById(savedBook.id)
        assertThat(foundBook).isPresent
        assertThat(foundBook.get().title).isEqualTo("Minimal Book")
        assertThat(foundBook.get().thumbnail).isNull()
        assertThat(foundBook.get().authors).isEmpty()
        assertThat(foundBook.get().translators).isEmpty()
        assertThat(foundBook.get().illustrators).isEmpty()
    }
}
