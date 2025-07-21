package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.UserAnswer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AnswerRepository : JpaRepository<UserAnswer<*>, Long> {
    fun findByQuizId(quizId: Long): List<UserAnswer<*>>

    fun findByUserIdAndQuizId(userId: String, quizId: Long): List<UserAnswer<*>>

    fun findByUserIdAndQuizIdOrderByAttemptNumberDesc(
            userId: String,
            quizId: Long
    ): List<UserAnswer<*>>

    @Query(
            "SELECT ua FROM UserAnswer ua WHERE ua.user.username = :userId AND ua.quiz.id = :quizId AND ua.attemptNumber = :attemptNumber"
    )
    fun findByUserIdAndQuizIdAndAttemptNumber(
            @Param("userId") userId: String,
            @Param("quizId") quizId: Long,
            @Param("attemptNumber") attemptNumber: Int
    ): UserAnswer<*>?

    fun countByUserIdAndQuizId(userId: String, quizId: Long): Long

    fun findByUserId(userId: String): List<UserAnswer<*>>
}
