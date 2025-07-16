package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.Authority
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AuthorityRepository : JpaRepository<Authority, Long> {
    fun findByName(name: String): Authority?
    
    @Query(value = "SELECT a.* FROM authorities a INNER JOIN user_authorities ua ON a.id = ua.authority_id WHERE ua.username = :username", nativeQuery = true)
    fun findAuthoritiesByUsername(@Param("username") username: String): List<Authority>
    
    fun existsByName(name: String): Boolean
}