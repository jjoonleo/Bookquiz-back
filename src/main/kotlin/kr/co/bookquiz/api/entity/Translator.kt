package kr.co.bookquiz.api.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@Entity
@Table(name = "translator")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class Translator(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String,

    @Column(unique = true, nullable = false)
    val name: String
)
