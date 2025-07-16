package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.authorities WHERE u.username = :username AND u.deleted = false")
    fun findByUsernameAndDeletedFalseWithAuthorities(@Param("username") username: String): User?
    
    fun findByUsernameAndDeletedFalse(username: String): User?
    fun findByEmailAndDeletedFalse(email: String): User?
    fun findByPhoneNumberAndDeletedFalse(phoneNumber: String): User?
    fun existsByUsernameAndDeletedFalse(username: String): Boolean
    fun existsByEmailAndDeletedFalse(email: String): Boolean
    fun existsByPhoneNumberAndDeletedFalse(phoneNumber: String): Boolean
}