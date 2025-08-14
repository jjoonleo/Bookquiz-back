package kr.co.bookquiz.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "books")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class Book(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
        @Column(nullable = false, length = 255) val title: String,
        @Column(nullable = true, length = 500) val subtitle: String?,
        @Column(nullable = false, length = 30, unique = true) val isbn: String,
        @Column(nullable = false, length = 255) val publisher: String,
        @Column(name = "quiz_price", nullable = false) val quizPrice: Int,
        @Column(name = "thumbnail", nullable = true, length = 1000) val thumbnail: String?,
        @Column(name = "max_attempt", nullable = false) val maxAttempt: Int = 3,
        @Column(name = "authors", nullable = false, length = 2000) val authors: String = "",
        @Column(name = "translators", nullable = false, length = 2000) val translators: String = "",
        @Column(name = "illustrators", nullable = false, length = 2000)
        val illustrators: String = ""
)
