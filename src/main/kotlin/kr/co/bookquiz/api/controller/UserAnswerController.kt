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

  @GetMapping("/user")
  fun getUserAnswers(
          @AuthenticationPrincipal userDetails: UserDetails
  ): ResponseEntity<ApiResponse<List<UserAnswerResponseDto>>> {
    val username = userDetails.username
    val answers = userAnswerService.getUserAnswersByUser(username)

    return ResponseEntity.ok(
            ApiResponse(
                    success = true,
                    message = "User answers retrieved successfully",
                    data = answers
            )
    )
  }

  @GetMapping("/quiz/{quizId}/latest")
  fun getLatestAnswer(
          @AuthenticationPrincipal userDetails: UserDetails,
          @PathVariable quizId: Long
  ): ResponseEntity<ApiResponse<UserAnswerResponseDto?>> {
    val username = userDetails.username
    val latestAnswer = userAnswerService.getLatestAnswer(username, quizId)

    return ResponseEntity.ok(
            ApiResponse(
                    success = true,
                    message = "Latest answer retrieved successfully",
                    data = latestAnswer
            )
    )
  }

  @GetMapping("/quiz/{quizId}/attempt-count")
  fun getAttemptCount(
          @AuthenticationPrincipal userDetails: UserDetails,
          @PathVariable quizId: Long
  ): ResponseEntity<ApiResponse<Long>> {
    val username = userDetails.username
    val attemptCount = userAnswerService.getAttemptCount(username, quizId)

    return ResponseEntity.ok(
            ApiResponse(
                    success = true,
                    message = "Attempt count retrieved successfully",
                    data = attemptCount
            )
    )
  }

  @GetMapping("/quiz/{quizId}/has-correct")
  fun hasCorrectAnswer(
          @AuthenticationPrincipal userDetails: UserDetails,
          @PathVariable quizId: Long
  ): ResponseEntity<ApiResponse<Boolean>> {
    val username = userDetails.username
    val hasCorrect = userAnswerService.hasCorrectAnswer(username, quizId)

    return ResponseEntity.ok(
            ApiResponse(
                    success = true,
                    message = "Correct answer status retrieved successfully",
                    data = hasCorrect
            )
    )
  }

  @GetMapping("/quiz/{quizId}/summary")
  fun getUserQuizSummary(
          @AuthenticationPrincipal userDetails: UserDetails,
          @PathVariable quizId: Long
  ): ResponseEntity<ApiResponse<UserQuizSummaryDto?>> {
    val username = userDetails.username
    val summary = userAnswerService.getUserQuizSummary(username, quizId)

    return ResponseEntity.ok(
            ApiResponse(
                    success = true,
                    message = "User quiz summary retrieved successfully",
                    data = summary
            )
    )
  }

  @GetMapping("/stats")
  fun getUserAnswerStats(
          @AuthenticationPrincipal userDetails: UserDetails
  ): ResponseEntity<ApiResponse<UserAnswerStatsDto>> {
    val username = userDetails.username
    val stats = userAnswerService.getUserAnswerStats(username)

    return ResponseEntity.ok(
            ApiResponse(
                    success = true,
                    message = "User answer statistics retrieved successfully",
                    data = stats
            )
    )
  }
}
