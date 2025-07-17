package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.Translator
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface TranslatorRepository : JpaRepository<Translator, Long> {
    fun findByName(name: String): Optional<Translator>
    
    @Query("SELECT COUNT(b) FROM Book b JOIN b.translators t WHERE t.id = :translatorId")
    fun countBooksByTranslatorId(@Param("translatorId") translatorId: Long): Long
}