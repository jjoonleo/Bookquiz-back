package kr.co.bookquiz.api.dto.book

import kr.co.bookquiz.api.entity.Book

// Extension functions for converting DTOs to entities
fun BookCreateRequest.toEntity(): Book {
    return Book(
            id = null, // Will be generated
            title = this.title,
            isbn = this.isbn,
            publisher = this.publisher,
            quizPrice = this.quizPrice,
            thumbnail = this.thumbnail,
            maxAttempt = this.maxAttempt,
            authors = this.authors,
            translators = this.translators,
            illustrators = this.illustrators
    )
}

fun BookUpdateRequest.toEntity(id: Long?): Book {
    return Book(
            id = id,
            title = this.title,
            isbn = this.isbn,
            publisher = this.publisher,
            quizPrice = this.quizPrice,
            thumbnail = this.thumbnail,
            maxAttempt = this.maxAttempt,
            authors = this.authors,
            translators = this.translators,
            illustrators = this.illustrators
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
            authors = this.authors,
            translators = this.translators,
            illustrators = this.illustrators,
            maxAttempt = this.maxAttempt
    )
}
