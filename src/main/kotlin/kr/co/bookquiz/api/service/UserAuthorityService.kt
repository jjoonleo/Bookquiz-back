package kr.co.bookquiz.api.service

import kr.co.bookquiz.api.api.exception.ApiException
import kr.co.bookquiz.api.api.exception.ErrorCode
import kr.co.bookquiz.api.entity.Authority
import kr.co.bookquiz.api.repository.AuthorityRepository
import kr.co.bookquiz.api.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserAuthorityService(
    private val userRepository: UserRepository,
    private val authorityRepository: AuthorityRepository
) {

    fun addAuthorityToUser(username: String, authorityName: String) {
        val user = userRepository.findByUsernameAndDeletedFalse(username)
            ?: throw ApiException(ErrorCode.USER_NOT_FOUND)

        val authority = authorityRepository.findByName(authorityName)
            ?: throw ApiException(ErrorCode.INTERNAL_ERROR, "Authority not found: $authorityName")

        if (!user.authorities.contains(authority)) {
            val updatedUser = user.copy(authorities = user.authorities + authority)
            userRepository.save(updatedUser)
        }
    }

    fun removeAuthorityFromUser(username: String, authorityName: String) {
        val user = userRepository.findByUsernameAndDeletedFalse(username)
            ?: throw ApiException(ErrorCode.USER_NOT_FOUND)

        val authority = authorityRepository.findByName(authorityName)
            ?: throw ApiException(ErrorCode.INTERNAL_ERROR, "Authority not found: $authorityName")

        if (user.authorities.contains(authority)) {
            val updatedUser = user.copy(authorities = user.authorities - authority)
            userRepository.save(updatedUser)
        }
    }

    fun getUserAuthorities(username: String): List<Authority> {
        val user = userRepository.findByUsernameAndDeletedFalse(username)
            ?: throw ApiException(ErrorCode.USER_NOT_FOUND)

        return user.authorities.toList()
    }

    fun createAuthorityIfNotExists(authorityName: String, description: String? = null): Authority {
        return authorityRepository.findByName(authorityName)
            ?: authorityRepository.save(Authority(name = authorityName, description = description))
    }

    fun userHasAuthority(username: String, authorityName: String): Boolean {
        val user = userRepository.findByUsernameAndDeletedFalse(username)
            ?: return false

        return user.authorities.any { it.name == authorityName }
    }
}