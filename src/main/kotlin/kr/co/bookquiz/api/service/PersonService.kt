package kr.co.bookquiz.api.service

import jakarta.transaction.Transactional
import kr.co.bookquiz.api.entity.Person
import kr.co.bookquiz.api.repository.PersonRepository
import org.springframework.stereotype.Service

@Service
@Transactional
class PersonService(
    private val personRepository: PersonRepository
) {

    fun findOrCreatePerson(name: String): Person {
        return personRepository.findByName(name).orElseGet {
            personRepository.save(Person(name = name))
        }
    }

    fun findOrCreatePersons(names: List<String>): List<Person> {
        return names.map { name ->
            findOrCreatePerson(name)
        }
    }

    fun cleanupUnusedPersons(oldPersons: List<Person>, newPersons: List<Person>?) {
        val removedPersons: List<Person> = if (newPersons != null) {
            oldPersons.filter { oldPerson ->
                newPersons.none { newPerson -> newPerson.id == oldPerson.id }
            }
        } else {
            oldPersons
        }

        removedPersons.forEach { person ->
            person.id?.let { personId ->
                if (isPersonUnused(personId)) {
                    personRepository.deleteById(personId)
                }
            }
        }
    }

    private fun isPersonUnused(personId: Long): Boolean {
        val authorCount = personRepository.countBooksByAuthorId(personId)
        val translatorCount = personRepository.countBooksByTranslatorId(personId)
        val illustratorCount = personRepository.countBooksByIllustratorId(personId)

        return authorCount == 0L && translatorCount == 0L && illustratorCount == 0L
    }
}