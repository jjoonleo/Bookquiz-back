package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.Quiz
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface QuizRepository : JpaRepository<Quiz<*>, Long> {

    fun findByBookId(bookId: Long): List<Quiz<*>>

    fun findByTitleContaining(title: String): List<Quiz<*>>

    @Query(
        "SELECT q FROM Quiz q WHERE " +
            "(:bookId IS NULL OR q.book.id = :bookId) AND " +
            "(:title IS NULL OR LOWER(q.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:type IS NULL OR TYPE(q) = :entityClass)"
    )
    fun findQuizzesWithFilters(
        @Param("bookId") bookId: Long?,
        @Param("title") title: String?,
        @Param("type") type: String?,
        @Param("entityClass") entityClass: Class<out Quiz<*>>?
    ): List<Quiz<*>>

    @Query("SELECT q FROM Quiz q WHERE q.book.id = :bookId AND TYPE(q) = :quizType")
    fun findByBookIdAndType(
        @Param("bookId") bookId: Long,
        @Param("quizType") quizType: Class<out Quiz<*>>
    ): List<Quiz<*>>
}