package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.Authority
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorityRepository : JpaRepository<Authority, Long> {
    fun findByUserUsername(username: String): List<Authority>
    fun deleteByUserUsername(username: String)
}