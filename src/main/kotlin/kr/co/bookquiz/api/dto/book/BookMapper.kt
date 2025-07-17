package kr.co.bookquiz.api.dto.book

import kr.co.bookquiz.api.entity.Author
import kr.co.bookquiz.api.entity.Book
import kr.co.bookquiz.api.entity.Illustrator
import kr.co.bookquiz.api.entity.Translator

// Extension functions for converting DTOs to entities
fun BookCreateRequest.toEntity(): Book {
    return Book(
        id = null, // Will be generated
        title = this.title,
        isbn = this.isbn,
        publisher = this.publisher,
        quizPrice = this.quizPrice,
        thumbnail = this.thumbnail
    )
}

fun BookUpdateRequest.toEntity(id: Long?): Book {
    return Book(
        id = id,
        title = this.title,
        isbn = this.isbn,
        publisher = this.publisher,
        quizPrice = this.quizPrice,
        thumbnail = this.thumbnail
    )
}

// Extension functions for converting entities to DTOs
fun Book.toResponse(): BookResponse {
    return BookResponse(
        id = this.id,
        title = this.title,
        isbn = this.isbn,
        publisher = this.publisher,
        quizPrice = this.quizPrice,
        thumbnail = this.thumbnail,
        authors = this.authors.map { it.toResponse() },
        translators = this.translators.map { it.toResponse() },
        illustrators = this.illustrators.map { it.toResponse() }
    )
}

fun Author.toResponse(): AuthorResponse {
    return AuthorResponse(
        id = this.id,
        name = this.name
    )
}

fun Translator.toResponse(): TranslatorResponse {
    return TranslatorResponse(
        id = this.id,
        name = this.name
    )
}

fun Illustrator.toResponse(): IllustratorResponse {
    return IllustratorResponse(
        id = this.id,
        name = this.name
    )
}