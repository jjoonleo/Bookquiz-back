package kr.co.bookquiz.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "books")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class Book(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 255)
    val title: String,

    @Column(nullable = false, length = 30, unique = true)
    val isbn: String,

    @Column(nullable = false, length = 255)
    val publisher: String,

    @Column(name = "quiz_price", nullable = false)
    val quizPrice: Int,

    @Column(name = "thumbnail", nullable = true, length = 1000)
    val thumbnail: String?,

    @ManyToMany
    @JoinTable(
        name = "book_author",
        joinColumns = [JoinColumn(name = "book_id")],
        inverseJoinColumns = [JoinColumn(name = "author_id")]
    )
    val authors: List<Author> = mutableListOf(),

    @ManyToMany
    @JoinTable(
        name = "book_translator",
        joinColumns = [JoinColumn(name = "book_id")],
        inverseJoinColumns = [JoinColumn(name = "translator_id")]
    )
    val translators: List<Translator> = mutableListOf(),

    @ManyToMany
    @JoinTable(
        name = "book_illustrator",
        joinColumns = [JoinColumn(name = "book_id")],
        inverseJoinColumns = [JoinColumn(name = "illustrator_id")]
    )
    val illustrators: List<Illustrator> = mutableListOf()
)