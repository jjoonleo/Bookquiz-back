package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {

    @EntityGraph(attributePaths = ["authorities"])
    fun findByUsernameAndDeletedFalse(username: String): User?
    fun findByEmailAndDeletedFalse(email: String): User?
    fun findByPhoneNumberAndDeletedFalse(phoneNumber: String): User?
    fun existsByUsernameAndDeletedFalse(username: String): Boolean
    fun existsByEmailAndDeletedFalse(email: String): Boolean
    fun existsByPhoneNumberAndDeletedFalse(phoneNumber: String): Boolean
}