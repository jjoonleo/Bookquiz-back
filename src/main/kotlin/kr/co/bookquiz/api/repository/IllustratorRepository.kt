package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.Illustrator
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface IllustratorRepository : JpaRepository<Illustrator, Long> {
    fun findByName(name: String): Optional<Illustrator>
    
    @Query("SELECT COUNT(b) FROM Book b JOIN b.illustrators i WHERE i.id = :illustratorId")
    fun countBooksByIllustratorId(@Param("illustratorId") illustratorId: Long): Long
}