package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.TestcontainersConfiguration
import kr.co.bookquiz.api.entity.Book
import kr.co.bookquiz.api.entity.Person
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@Import(TestcontainersConfiguration::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    fun `should find all books returns empty list initially`() {
        // Given - no books
        
        // When
        val allBooks = bookRepository.findAll()

        // Then
        assertThat(allBooks).isEmpty()
    }

    @Test
    fun `should count books returns zero initially`() {
        // Given - no books
        val initialCount = bookRepository.count()

        // When - count books
        // Then
        assertThat(initialCount).isEqualTo(0)
    }

    @Test
    fun `should find book by non-existent id returns empty`() {
        // Given - no books
        
        // When
        val foundBook = bookRepository.findById(999L)

        // Then
        assertThat(foundBook).isNotPresent
    }

    @Test
    fun `should delete by non-existent id does not throw exception`() {
        // Given - no books
        
        // When & Then - should not throw exception
        bookRepository.deleteById(999L)
    }

    @Test
    fun `should save and find book by id`() {
        // Given
        val book = Book(
            title = "Test Book",
            isbn = "9780123456789",
            publisher = "Test Publisher",
            quizPrice = 1000,
            thumbnail = "https://example.com/thumbnail.jpg"
        )

        // When
        val savedBook = bookRepository.save(book)

        // Then
        assertThat(savedBook.id).isNotNull
        val foundBook = bookRepository.findById(savedBook.id!!)
        assertThat(foundBook).isPresent
        assertThat(foundBook.get().title).isEqualTo("Test Book")
        assertThat(foundBook.get().isbn).isEqualTo("9780123456789")
        assertThat(foundBook.get().publisher).isEqualTo("Test Publisher")
        assertThat(foundBook.get().quizPrice).isEqualTo(1000)
        assertThat(foundBook.get().thumbnail).isEqualTo("https://example.com/thumbnail.jpg")
    }

    @Test
    fun `should save book with authors, translators, and illustrators`() {
        // Given
        val author = Person(name = "Test Author")
        val translator = Person(name = "Test Translator")
        val illustrator = Person(name = "Test Illustrator")
        
        val savedAuthor = testEntityManager.persistAndFlush(author)
        val savedTranslator = testEntityManager.persistAndFlush(translator)
        val savedIllustrator = testEntityManager.persistAndFlush(illustrator)
        
        val book = Book(
            title = "Book with Relations",
            isbn = "9780123456790",
            publisher = "Relation Publisher",
            quizPrice = 2000,
            thumbnail = "https://example.com/relations.jpg",
            authors = listOf(savedAuthor),
            translators = listOf(savedTranslator),
            illustrators = listOf(savedIllustrator)
        )

        // When
        val savedBook = bookRepository.save(book)

        // Then
        assertThat(savedBook.id).isNotNull
        val foundBook = bookRepository.findById(savedBook.id!!)
        assertThat(foundBook).isPresent
        
        val retrievedBook = foundBook.get()
        assertThat(retrievedBook.title).isEqualTo("Book with Relations")
        assertThat(retrievedBook.authors).hasSize(1)
        assertThat(retrievedBook.authors[0].name).isEqualTo("Test Author")
        assertThat(retrievedBook.translators).hasSize(1)
        assertThat(retrievedBook.translators[0].name).isEqualTo("Test Translator")
        assertThat(retrievedBook.illustrators).hasSize(1)
        assertThat(retrievedBook.illustrators[0].name).isEqualTo("Test Illustrator")
    }

    @Test
    fun `should update book`() {
        // Given
        val book = Book(
            title = "Original Title",
            isbn = "9780123456791",
            publisher = "Update Publisher",
            quizPrice = 1500,
            thumbnail = "https://example.com/original.jpg"
        )
        val savedBook = bookRepository.save(book)

        // When
        val updatedBook = savedBook.copy(
            title = "Updated Test Book",
            quizPrice = 2000
        )
        bookRepository.save(updatedBook)

        // Then
        val foundBook = bookRepository.findById(savedBook.id!!)
        assertThat(foundBook).isPresent
        assertThat(foundBook.get().title).isEqualTo("Updated Test Book")
        assertThat(foundBook.get().quizPrice).isEqualTo(2000)
    }

    @Test
    fun `should delete book`() {
        // Given
        val book = Book(
            title = "Book to Delete",
            isbn = "9780123456792",
            publisher = "Delete Publisher",
            quizPrice = 1000,
            thumbnail = "https://example.com/delete.jpg"
        )
        val savedBook = bookRepository.save(book)

        // When
        bookRepository.deleteById(savedBook.id!!)

        // Then
        val foundBook = bookRepository.findById(savedBook.id!!)
        assertThat(foundBook).isNotPresent
    }

    @Test
    fun `should find all books`() {
        // Given
        val book1 = Book(
            title = "First Book",
            isbn = "9780123456793",
            publisher = "List Publisher",
            quizPrice = 1000,
            thumbnail = "https://example.com/first.jpg"
        )
        val book2 = Book(
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
        // Given
        val bookWithoutOptional = Book(
            title = "Minimal Book",
            isbn = "9780123456795",
            publisher = "Minimal Publisher",
            quizPrice = 500,
            thumbnail = null
        )

        // When
        val savedBook = bookRepository.save(bookWithoutOptional)

        // Then
        assertThat(savedBook.id).isNotNull
        val foundBook = bookRepository.findById(savedBook.id!!)
        assertThat(foundBook).isPresent
        assertThat(foundBook.get().title).isEqualTo("Minimal Book")
        assertThat(foundBook.get().thumbnail).isNull()
        assertThat(foundBook.get().authors).isEmpty()
        assertThat(foundBook.get().translators).isEmpty()
        assertThat(foundBook.get().illustrators).isEmpty()
    }

    @Test
    fun `should save multiple books with sequential IDs`() {
        // Given
        val book1 = Book(
            title = "Sequential Test 1",
            isbn = "9780123456797",
            publisher = "Sequential Publisher",
            quizPrice = 1000,
            thumbnail = "https://example.com/seq1.jpg"
        )
        val book2 = Book(
            title = "Sequential Test 2",
            isbn = "9780123456798",
            publisher = "Sequential Publisher",
            quizPrice = 1500,
            thumbnail = "https://example.com/seq2.jpg"
        )

        // When
        val savedBook1 = bookRepository.save(book1)
        val savedBook2 = bookRepository.save(book2)

        // Then
        assertThat(savedBook1.id).isNotNull
        assertThat(savedBook2.id).isNotNull
        assertThat(savedBook1.id).isLessThan(savedBook2.id)
        assertThat(savedBook2.id!! - savedBook1.id!!).isEqualTo(1L)
    }

    @Test
    fun `should handle book with multiple authors`() {
        // Given
        val author1 = Person(name = "First Author")
        val author2 = Person(name = "Second Author")
        val savedAuthor1 = testEntityManager.persistAndFlush(author1)
        val savedAuthor2 = testEntityManager.persistAndFlush(author2)
        
        val book = Book(
            title = "Multi-Author Book",
            isbn = "9780123456799",
            publisher = "Multi Publisher",
            quizPrice = 2000,
            thumbnail = "https://example.com/multi.jpg",
            authors = listOf(savedAuthor1, savedAuthor2)
        )

        // When
        val savedBook = bookRepository.save(book)

        // Then
        assertThat(savedBook.id).isNotNull
        val foundBook = bookRepository.findById(savedBook.id!!)
        assertThat(foundBook).isPresent
        assertThat(foundBook.get().authors).hasSize(2)
        assertThat(foundBook.get().authors.map { it.name }).containsExactlyInAnyOrder("First Author", "Second Author")
    }

    @Test
    fun `should handle book with multiple translators and illustrators`() {
        // Given
        val translator1 = Person(name = "First Translator")
        val translator2 = Person(name = "Second Translator")
        val illustrator1 = Person(name = "First Illustrator")
        val illustrator2 = Person(name = "Second Illustrator")
        
        val savedTranslator1 = testEntityManager.persistAndFlush(translator1)
        val savedTranslator2 = testEntityManager.persistAndFlush(translator2)
        val savedIllustrator1 = testEntityManager.persistAndFlush(illustrator1)
        val savedIllustrator2 = testEntityManager.persistAndFlush(illustrator2)
        
        val book = Book(
            title = "Multi-Collaborator Book",
            isbn = "9780123456800",
            publisher = "Collaborator Publisher",
            quizPrice = 2500,
            thumbnail = "https://example.com/collab.jpg",
            translators = listOf(savedTranslator1, savedTranslator2),
            illustrators = listOf(savedIllustrator1, savedIllustrator2)
        )

        // When
        val savedBook = bookRepository.save(book)

        // Then
        assertThat(savedBook.id).isNotNull
        val foundBook = bookRepository.findById(savedBook.id!!)
        assertThat(foundBook).isPresent
        
        val retrievedBook = foundBook.get()
        assertThat(retrievedBook.translators).hasSize(2)
        assertThat(retrievedBook.translators.map { it.name }).containsExactlyInAnyOrder("First Translator", "Second Translator")
        assertThat(retrievedBook.illustrators).hasSize(2)
        assertThat(retrievedBook.illustrators.map { it.name }).containsExactlyInAnyOrder("First Illustrator", "Second Illustrator")
    }
}
