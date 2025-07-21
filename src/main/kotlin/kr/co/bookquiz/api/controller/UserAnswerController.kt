package kr.co.bookquiz.api.controller

import kr.co.bookquiz.api.api.response.ApiResponse
import kr.co.bookquiz.api.dto.useranswer.*
import kr.co.bookquiz.api.service.UserAnswerService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user-answers")
class UserAnswerController(private val userAnswerService: UserAnswerService) {

  @PostMapping
  fun submitUserAnswer(
          @AuthenticationPrincipal userDetails: UserDetails,
          @RequestBody createDto: CreateUserAnswerDto
  ): ResponseEntity<ApiResponse<UserAnswerResponseDto>> {

    val savedAnswer = userAnswerService.createUserAnswer(createDto, userDetails.username)

    return ResponseEntity.ok(
            ApiResponse(
                    success = true,
                    message = "User answer created successfully",
                    data = savedAnswer
            )
    )
  }

  @PutMapping("/{id}")
  fun updateUserAnswer(
          @PathVariable id: Long,
          @RequestBody updateDto: UpdateUserAnswerDto
  ): ResponseEntity<ApiResponse<UserAnswerResponseDto>> {
    val updatedAnswer = userAnswerService.updateUserAnswer(id, updateDto)

    return ResponseEntity.ok(
            ApiResponse(
                    success = true,
                    message = "User answer updated successfully",
                    data = updatedAnswer
            )
    )
  }

  @GetMapping("/quiz/{quizId}")
  fun getUserAnswersByQuiz(
          @AuthenticationPrincipal userDetails: UserDetails,
          @PathVariable quizId: Long
  ): ResponseEntity<ApiResponse<List<UserAnswerResponseDto>>> {
    val username = userDetails.username
    val answers = userAnswerService.getUserAnswersByQuiz(username, quizId)

    return ResponseEntity.ok(
            ApiResponse(
                    success = true,
                    message = "User answers retrieved successfully",
                    data = answers
            )
    )
  }
}
