package kr.co.bookquiz.api.service

import kr.co.bookquiz.api.entity.Person
import kr.co.bookquiz.api.repository.PersonRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class PersonServiceTest {

    private lateinit var personService: PersonService
    private lateinit var personRepository: PersonRepository

    @BeforeEach
    fun setUp() {
        personRepository = mock(PersonRepository::class.java)
        personService = PersonService(personRepository)
    }

    @Test
    fun `should return existing person when found`() {
        // Given
        val existingPerson = Person(id = 1L, name = "John Doe")
        given(personRepository.findByName("John Doe")).willReturn(Optional.of(existingPerson))

        // When
        val result = personService.findOrCreatePerson("John Doe")

        // Then
        assertThat(result).isEqualTo(existingPerson)
        then(personRepository).should().findByName("John Doe")
        then(personRepository).should(never()).save(any(Person::class.java))
    }

    @Test
    fun `should create new person when not found`() {
        // Given
        val newPerson = Person(id = 2L, name = "Jane Doe")
        given(personRepository.findByName("Jane Doe")).willReturn(Optional.empty())
        given(personRepository.save(any(Person::class.java))).willReturn(newPerson)

        // When
        val result = personService.findOrCreatePerson("Jane Doe")

        // Then
        assertThat(result).isEqualTo(newPerson)
        then(personRepository).should().findByName("Jane Doe")
        then(personRepository).should().save(any(Person::class.java))
    }

    @Test
    fun `should find or create multiple persons`() {
        // Given
        val person1 = Person(id = 1L, name = "Person 1")
        val person2 = Person(id = 2L, name = "Person 2")
        val names = listOf("Person 1", "Person 2")

        given(personRepository.findByName("Person 1")).willReturn(Optional.of(person1))
        given(personRepository.findByName("Person 2")).willReturn(Optional.empty())
        given(personRepository.save(any(Person::class.java))).willReturn(person2)

        // When
        val result = personService.findOrCreatePersons(names)

        // Then
        assertThat(result).hasSize(2)
        assertThat(result[0]).isEqualTo(person1)
        assertThat(result[1]).isEqualTo(person2)
        then(personRepository).should().findByName("Person 1")
        then(personRepository).should().findByName("Person 2")
        then(personRepository).should().save(any(Person::class.java))
    }

    @Test
    fun `should cleanup unused persons`() {
        // Given
        val person1 = Person(id = 1L, name = "Person 1")
        val person2 = Person(id = 2L, name = "Person 2")
        val person3 = Person(id = 3L, name = "Person 3")

        val oldPersons = listOf(person1, person2, person3)
        val newPersons = listOf(person1) // person2 and person3 are removed

        given(personRepository.countBooksByAuthorId(2L)).willReturn(0L)
        given(personRepository.countBooksByTranslatorId(2L)).willReturn(0L)
        given(personRepository.countBooksByIllustratorId(2L)).willReturn(0L)

        given(personRepository.countBooksByAuthorId(3L))
            .willReturn(1L) // person3 is still used as author
        given(personRepository.countBooksByTranslatorId(3L)).willReturn(0L)
        given(personRepository.countBooksByIllustratorId(3L)).willReturn(0L)

        // When
        personService.cleanupUnusedPersons(oldPersons, newPersons)

        // Then
        then(personRepository).should().deleteById(2L) // person2 should be deleted
        then(personRepository).should(never()).deleteById(3L) // person3 should not be deleted
        then(personRepository).should(never()).deleteById(1L) // person1 should not be deleted
    }

    @Test
    fun `should cleanup all persons when newPersons is null`() {
        // Given
        val person1 = Person(id = 1L, name = "Person 1")
        val person2 = Person(id = 2L, name = "Person 2")
        val oldPersons = listOf(person1, person2)

        given(personRepository.countBooksByAuthorId(1L)).willReturn(0L)
        given(personRepository.countBooksByTranslatorId(1L)).willReturn(0L)
        given(personRepository.countBooksByIllustratorId(1L)).willReturn(0L)

        given(personRepository.countBooksByAuthorId(2L)).willReturn(0L)
        given(personRepository.countBooksByTranslatorId(2L)).willReturn(0L)
        given(personRepository.countBooksByIllustratorId(2L)).willReturn(0L)

        // When
        personService.cleanupUnusedPersons(oldPersons, null)

        // Then
        then(personRepository).should().deleteById(1L)
        then(personRepository).should().deleteById(2L)
    }

    @Test
    fun `should not delete person that is used in other books`() {
        // Given
        val person1 = Person(id = 1L, name = "Person 1")
        val person2 = Person(id = 2L, name = "Person 2")
        val person3 = Person(id = 3L, name = "Person 3")

        val oldPersons = listOf(person1, person2, person3)
        val newPersons = listOf<Person>() // All persons are removed from current book

        // person1 is still used as author in another book
        given(personRepository.countBooksByAuthorId(1L)).willReturn(1L)
        given(personRepository.countBooksByTranslatorId(1L)).willReturn(0L)
        given(personRepository.countBooksByIllustratorId(1L)).willReturn(0L)

        // person2 is still used as translator in another book
        given(personRepository.countBooksByAuthorId(2L)).willReturn(0L)
        given(personRepository.countBooksByTranslatorId(2L)).willReturn(1L)
        given(personRepository.countBooksByIllustratorId(2L)).willReturn(0L)

        // person3 is still used as illustrator in another book
        given(personRepository.countBooksByAuthorId(3L)).willReturn(0L)
        given(personRepository.countBooksByTranslatorId(3L)).willReturn(0L)
        given(personRepository.countBooksByIllustratorId(3L)).willReturn(1L)

        // When
        personService.cleanupUnusedPersons(oldPersons, newPersons)

        // Then
        then(personRepository)
            .should(never())
            .deleteById(1L) // person1 should not be deleted (used as author)
        then(personRepository)
            .should(never())
            .deleteById(2L) // person2 should not be deleted (used as translator)
        then(personRepository)
            .should(never())
            .deleteById(3L) // person3 should not be deleted (used as illustrator)
    }
}