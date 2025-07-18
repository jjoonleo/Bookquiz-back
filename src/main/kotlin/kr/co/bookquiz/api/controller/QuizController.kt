package kr.co.bookquiz.api.controller

import kr.co.bookquiz.api.dto.quiz.*
import kr.co.bookquiz.api.entity.*
import kr.co.bookquiz.api.service.QuizService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/quiz")
class QuizController(private val quizService: QuizService) {
  @GetMapping("/{quizId}")
  fun getQuiz(@PathVariable quizId: Long): ResponseEntity<Quiz> {
    return ResponseEntity.ok(quizService.getQuiz(quizId))
  }

  @GetMapping("/book/{bookId}")
  fun getQuizzesByBookId(@PathVariable bookId: Long): ResponseEntity<List<QuizResponseDto>> {
    return ResponseEntity.ok(quizService.getQuizzesByBookId(bookId))
  }

  @PostMapping("/{quizId}/evaluate/multiple-choice")
  fun evaluateMultipleChoice(
          @PathVariable quizId: Long,
          @RequestBody userAnswer: MultipleChoiceAnswerData
  ): ResponseEntity<AnswerResult> {
    return ResponseEntity.ok(quizService.evaluateQuizAnswer(quizId, userAnswer))
  }

  @PostMapping("/{quizId}/evaluate/subjective")
  fun evaluateSubjective(
          @PathVariable quizId: Long,
          @RequestBody userAnswer: SubjectiveAnswerData
  ): ResponseEntity<AnswerResult> {
    return ResponseEntity.ok(quizService.evaluateQuizAnswer(quizId, userAnswer))
  }

  @PostMapping("/{quizId}/evaluate/true-false")
  fun evaluateTrueFalse(
          @PathVariable quizId: Long,
          @RequestBody userAnswer: TrueFalseAnswerData
  ): ResponseEntity<AnswerResult> {
    return ResponseEntity.ok(quizService.evaluateQuizAnswer(quizId, userAnswer))
  }

  @GetMapping("/{quizId}/answer/multiple-choice")
  fun getMultipleChoiceAnswer(
          @PathVariable quizId: Long
  ): ResponseEntity<MultipleChoiceAnswerEntity?> {
    return ResponseEntity.ok(quizService.getMultipleChoiceAnswer(quizId))
  }

  @GetMapping("/{quizId}/answer/subjective")
  fun getSubjectiveAnswer(@PathVariable quizId: Long): ResponseEntity<SubjectiveAnswerEntity?> {
    return ResponseEntity.ok(quizService.getSubjectiveAnswer(quizId))
  }

  @GetMapping("/{quizId}/answer/true-false")
  fun getTrueFalseAnswer(@PathVariable quizId: Long): ResponseEntity<TrueFalseAnswerEntity?> {
    return ResponseEntity.ok(quizService.getTrueFalseAnswer(quizId))
  }
}
