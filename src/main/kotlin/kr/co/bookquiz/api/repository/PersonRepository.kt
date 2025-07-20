package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.Person
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface PersonRepository : JpaRepository<Person, Long> {
    fun findByName(name: String): Optional<Person>

    @Query("SELECT COUNT(b) FROM Book b JOIN b.authors a WHERE a.id = :personId")
    fun countBooksByAuthorId(@Param("personId") personId: Long): Long

    @Query("SELECT COUNT(b) FROM Book b JOIN b.translators t WHERE t.id = :personId")
    fun countBooksByTranslatorId(@Param("personId") personId: Long): Long

    @Query("SELECT COUNT(b) FROM Book b JOIN b.illustrators i WHERE i.id = :personId")
    fun countBooksByIllustratorId(@Param("personId") personId: Long): Long
}