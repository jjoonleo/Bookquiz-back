package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.Author
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface AuthorRepository : JpaRepository<Author, Long> {
    fun findByName(name: String): Optional<Author>
    
    @Query("SELECT COUNT(b) FROM Book b JOIN b.authors a WHERE a.id = :authorId")
    fun countBooksByAuthorId(@Param("authorId") authorId: Long): Long
}