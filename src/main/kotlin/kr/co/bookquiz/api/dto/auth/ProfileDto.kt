package kr.co.bookquiz.api.dto.auth

import java.time.LocalDateTime
import kr.co.bookquiz.api.entity.enums.Grade
import kr.co.bookquiz.api.entity.enums.Province

data class ProfileResponse(
        val username: String,
        val name: String,
        val email: String,
        val phoneNumber: String,
        val dateOfBirth: LocalDateTime,
        val province: Province,
        val grade: Grade,
        val gender: Boolean,
        val lastLogin: LocalDateTime?,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
        val authorities: List<String>
)
