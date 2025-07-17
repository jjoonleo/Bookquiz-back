package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.TestcontainersConfiguration
import kr.co.bookquiz.api.entity.Book
import kr.co.bookquiz.api.entity.Person
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
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
class PersonRepositoryTest {

    @Autowired
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    // Common test data
    private lateinit var testAuthor: Person
    private lateinit var testTranslator: Person
    private lateinit var testIllustrator: Person
    private lateinit var testPerson: Person

    @BeforeEach
    fun setUp() {
        // Create common test persons
        testAuthor = Person(name = "Test Author")
        testTranslator = Person(name = "Test Translator")
        testIllustrator = Person(name = "Test Illustrator")
        testPerson = Person(name = "Test Person")
    }

    private fun createTestBook(
        title: String,
        isbn: String,
        authors: List<Person> = emptyList(),
        translators: List<Person> = emptyList(),
        illustrators: List<Person> = emptyList()
    ): Book {
        return Book(
            title = title,
            isbn = isbn,
            publisher = "Test Publisher",
            quizPrice = 1000,
            authors = authors,
            translators = translators,
            illustrators = illustrators,
            thumbnail = "https://example.com/thumb.jpg"
        )
    }

    @Test
    fun `should find all persons returns empty list initially`() {
        // Given - no persons
        
        // When
        val allPersons = personRepository.findAll()

        // Then
        assertThat(allPersons).isEmpty()
    }

    @Test
    fun `should count persons returns zero initially`() {
        // Given - no persons
        val initialCount = personRepository.count()

        // When - count persons
        // Then
        assertThat(initialCount).isEqualTo(0)
    }

    @Test
    fun `should find person by non-existent id returns empty`() {
        // Given - no persons
        
        // When
        val foundPerson = personRepository.findById(999L)

        // Then
        assertThat(foundPerson).isNotPresent
    }

    @Test
    fun `should delete by non-existent id does not throw exception`() {
        // Given - no persons
        
        // When & Then - should not throw exception
        personRepository.deleteById(999L)
    }

    @Test
    fun `should save and find person by id`() {
        // Given
        // When
        val savedPerson = personRepository.save(testPerson)

        // Then
        assertThat(savedPerson.id).isNotNull
        val foundPerson = personRepository.findById(savedPerson.id!!)
        assertThat(foundPerson).isPresent
        assertThat(foundPerson.get().name).isEqualTo("Test Person")
    }

    @Test
    fun `should find person by name when exists`() {
        // Given
        val savedPerson = personRepository.save(testPerson)

        // When
        val foundPerson = personRepository.findByName("Test Person")

        // Then
        assertThat(foundPerson).isPresent
        assertThat(foundPerson.get().id).isEqualTo(savedPerson.id)
        assertThat(foundPerson.get().name).isEqualTo("Test Person")
    }

    @Test
    fun `should find by name returns empty when name does not exist`() {
        // Given
        personRepository.save(testPerson)

        // When
        val foundPerson = personRepository.findByName("Non Existent Person")

        // Then
        assertThat(foundPerson).isNotPresent
    }

    @Test
    fun `should find by name returns empty when name is empty`() {
        // Given
        personRepository.save(testPerson)

        // When
        val foundPerson = personRepository.findByName("")

        // Then
        assertThat(foundPerson).isNotPresent
    }

    @Test
    fun `should find by name is case sensitive`() {
        // Given
        personRepository.save(testPerson)

        // When
        val foundPerson = personRepository.findByName("test person")

        // Then
        assertThat(foundPerson).isNotPresent
    }

    @Test
    fun `should count books by author id returns correct count`() {
        // Given
        val savedAuthor = testEntityManager.persistAndFlush(testAuthor)
        
        val book1 = createTestBook("Book 1", "9780123456789", authors = listOf(savedAuthor))
        val book2 = createTestBook("Book 2", "9780123456790", authors = listOf(savedAuthor))
        
        testEntityManager.persistAndFlush(book1)
        testEntityManager.persistAndFlush(book2)

        // When
        val count = personRepository.countBooksByAuthorId(savedAuthor.id!!)

        // Then
        assertThat(count).isEqualTo(2)
    }

    @Test
    fun `should count books by author id returns zero when person is not author`() {
        // Given
        val savedAuthor = testEntityManager.persistAndFlush(testAuthor)
        val savedNonAuthor = testEntityManager.persistAndFlush(testPerson)
        
        val book = createTestBook("Test Book", "9780123456789", authors = listOf(savedAuthor))
        testEntityManager.persistAndFlush(book)

        // When
        val count = personRepository.countBooksByAuthorId(savedNonAuthor.id!!)

        // Then
        assertThat(count).isEqualTo(0)
    }

    @Test
    fun `should count books by author id returns zero when person does not exist`() {
        // Given - no persons
        
        // When
        val count = personRepository.countBooksByAuthorId(999L)

        // Then
        assertThat(count).isEqualTo(0)
    }

    @Test
    fun `should count books by translator id returns correct count`() {
        // Given
        val savedTranslator = testEntityManager.persistAndFlush(testTranslator)
        
        val book1 = createTestBook("Book 1", "9780123456789", translators = listOf(savedTranslator))
        val book2 = createTestBook("Book 2", "9780123456790", translators = listOf(savedTranslator))
        
        testEntityManager.persistAndFlush(book1)
        testEntityManager.persistAndFlush(book2)

        // When
        val count = personRepository.countBooksByTranslatorId(savedTranslator.id!!)

        // Then
        assertThat(count).isEqualTo(2)
    }

    @Test
    fun `should count books by translator id returns zero when person is not translator`() {
        // Given
        val savedTranslator = testEntityManager.persistAndFlush(testTranslator)
        val savedNonTranslator = testEntityManager.persistAndFlush(testPerson)
        
        val book = createTestBook("Test Book", "9780123456789", translators = listOf(savedTranslator))
        testEntityManager.persistAndFlush(book)

        // When
        val count = personRepository.countBooksByTranslatorId(savedNonTranslator.id!!)

        // Then
        assertThat(count).isEqualTo(0)
    }

    @Test
    fun `should count books by translator id returns zero when person does not exist`() {
        // Given - no persons
        
        // When
        val count = personRepository.countBooksByTranslatorId(999L)

        // Then
        assertThat(count).isEqualTo(0)
    }

    @Test
    fun `should count books by illustrator id returns correct count`() {
        // Given
        val savedIllustrator = testEntityManager.persistAndFlush(testIllustrator)
        
        val book1 = createTestBook("Book 1", "9780123456789", illustrators = listOf(savedIllustrator))
        val book2 = createTestBook("Book 2", "9780123456790", illustrators = listOf(savedIllustrator))
        
        testEntityManager.persistAndFlush(book1)
        testEntityManager.persistAndFlush(book2)

        // When
        val count = personRepository.countBooksByIllustratorId(savedIllustrator.id!!)

        // Then
        assertThat(count).isEqualTo(2)
    }

    @Test
    fun `should count books by illustrator id returns zero when person is not illustrator`() {
        // Given
        val savedIllustrator = testEntityManager.persistAndFlush(testIllustrator)
        val savedNonIllustrator = testEntityManager.persistAndFlush(testPerson)
        
        val book = createTestBook("Test Book", "9780123456789", illustrators = listOf(savedIllustrator))
        testEntityManager.persistAndFlush(book)

        // When
        val count = personRepository.countBooksByIllustratorId(savedNonIllustrator.id!!)

        // Then
        assertThat(count).isEqualTo(0)
    }

    @Test
    fun `should count books by illustrator id returns zero when person does not exist`() {
        // Given - no persons
        
        // When
        val count = personRepository.countBooksByIllustratorId(999L)

        // Then
        assertThat(count).isEqualTo(0)
    }

    @Test
    fun `should handle person with multiple roles in same book`() {
        // Given
        val savedPerson = testEntityManager.persistAndFlush(testPerson)
        
        val book = createTestBook(
            title = "Multi-Role Book",
            isbn = "9780123456789",
            authors = listOf(savedPerson),
            translators = listOf(savedPerson),
            illustrators = listOf(savedPerson)
        )
        testEntityManager.persistAndFlush(book)

        // When
        val authorCount = personRepository.countBooksByAuthorId(savedPerson.id!!)
        val translatorCount = personRepository.countBooksByTranslatorId(savedPerson.id)
        val illustratorCount = personRepository.countBooksByIllustratorId(savedPerson.id)

        // Then
        assertThat(authorCount).isEqualTo(1)
        assertThat(translatorCount).isEqualTo(1)
        assertThat(illustratorCount).isEqualTo(1)
    }

    @Test
    fun `should handle person with different roles in different books`() {
        // Given
        val savedPerson = testEntityManager.persistAndFlush(testPerson)
        
        val book1 = createTestBook("Authored Book", "9780123456789", authors = listOf(savedPerson))
        val book2 = createTestBook("Translated Book", "9780123456790", translators = listOf(savedPerson))
        val book3 = createTestBook("Illustrated Book", "9780123456791", illustrators = listOf(savedPerson))
        
        testEntityManager.persistAndFlush(book1)
        testEntityManager.persistAndFlush(book2)
        testEntityManager.persistAndFlush(book3)

        // When
        val authorCount = personRepository.countBooksByAuthorId(savedPerson.id!!)
        val translatorCount = personRepository.countBooksByTranslatorId(savedPerson.id)
        val illustratorCount = personRepository.countBooksByIllustratorId(savedPerson.id)

        // Then
        assertThat(authorCount).isEqualTo(1)
        assertThat(translatorCount).isEqualTo(1)
        assertThat(illustratorCount).isEqualTo(1)
    }

    @Test
    fun `should count books correctly when person is in multiple author collaborations`() {
        // Given
        val coAuthor1 = Person(name = "Co-Author 1")
        val coAuthor2 = Person(name = "Co-Author 2")
        
        val savedPerson = testEntityManager.persistAndFlush(testPerson)
        val savedCoAuthor1 = testEntityManager.persistAndFlush(coAuthor1)
        val savedCoAuthor2 = testEntityManager.persistAndFlush(coAuthor2)
        
        val book1 = createTestBook("Collaboration Book 1", "9780123456789", authors = listOf(savedPerson, savedCoAuthor1))
        val book2 = createTestBook("Collaboration Book 2", "9780123456790", authors = listOf(savedPerson, savedCoAuthor2))
        
        testEntityManager.persistAndFlush(book1)
        testEntityManager.persistAndFlush(book2)

        // When
        val count = personRepository.countBooksByAuthorId(savedPerson.id!!)

        // Then
        assertThat(count).isEqualTo(2)
    }

    @Test
    fun `should update person`() {
        // Given
        val savedPerson = personRepository.save(testPerson)

        // When
        val updatedPerson = savedPerson.copy(name = "Updated Name")
        personRepository.save(updatedPerson)

        // Then
        val foundPerson = personRepository.findById(savedPerson.id!!)
        assertThat(foundPerson).isPresent
        assertThat(foundPerson.get().name).isEqualTo("Updated Name")
    }

    @Test
    fun `should delete person`() {
        // Given
        val savedPerson = personRepository.save(testPerson)

        // When
        personRepository.deleteById(savedPerson.id!!)

        // Then
        val foundPerson = personRepository.findById(savedPerson.id)
        assertThat(foundPerson).isNotPresent
    }

    @Test
    fun `should find all persons`() {
        // Given
        val person1 = Person(name = "First Person")
        val person2 = Person(name = "Second Person")
        
        personRepository.save(person1)
        personRepository.save(person2)

        // When
        val allPersons = personRepository.findAll()

        // Then
        assertThat(allPersons).hasSize(2)
        assertThat(allPersons.map { it.name }).containsExactlyInAnyOrder("First Person", "Second Person")
    }

    @Test
    fun `should save multiple persons with sequential IDs`() {
        // Given
        val person1 = Person(name = "Sequential Test 1")
        val person2 = Person(name = "Sequential Test 2")

        // When
        val savedPerson1 = personRepository.save(person1)
        val savedPerson2 = personRepository.save(person2)

        // Then
        assertThat(savedPerson1.id).isNotNull
        assertThat(savedPerson2.id).isNotNull
        assertThat(savedPerson1.id).isLessThan(savedPerson2.id)
        assertThat(savedPerson2.id!! - savedPerson1.id!!).isEqualTo(1L)
    }
}
