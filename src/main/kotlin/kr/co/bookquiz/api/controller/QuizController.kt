package kr.co.bookquiz.api.controller

import kr.co.bookquiz.api.dto.quiz.*
import kr.co.bookquiz.api.service.AnswerResult
import kr.co.bookquiz.api.service.QuizService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/quiz")
class QuizController(private val quizService: QuizService) {

    @PostMapping
    fun createQuiz(@RequestBody createQuizDto: CreateQuizDto): ResponseEntity<QuizResponseDto> {
        return ResponseEntity.ok(quizService.createQuiz(createQuizDto))
    }

    @GetMapping("/{quizId}")
    fun getQuiz(@PathVariable quizId: Long): ResponseEntity<QuizResponseDto> {
        return ResponseEntity.ok(quizService.getQuiz(quizId))
    }

    @PutMapping("/{quizId}")
    fun updateQuiz(
        @PathVariable quizId: Long,
        @RequestBody updateQuizDto: UpdateQuizDto
    ): ResponseEntity<QuizResponseDto> {
        return ResponseEntity.ok(quizService.updateQuiz(quizId, updateQuizDto))
    }

    @DeleteMapping("/{quizId}")
    fun deleteQuiz(@PathVariable quizId: Long): ResponseEntity<Unit> {
        quizService.deleteQuiz(quizId)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/book/{bookId}")
    fun getQuizzesByBookId(@PathVariable bookId: Long): ResponseEntity<List<QuizResponseDto>> {
        return ResponseEntity.ok(quizService.getQuizzesByBookId(bookId))
    }

    @GetMapping("/search")
    fun searchQuizzesByTitle(@RequestParam title: String): ResponseEntity<List<QuizResponseDto>> {
        return ResponseEntity.ok(quizService.getQuizzesByTitle(title))
    }

    @GetMapping("/filter")
    fun getQuizzesWithFilters(
        @ModelAttribute filter: QuizFilterDto
    ): ResponseEntity<List<QuizResponseDto>> {
        return ResponseEntity.ok(quizService.getQuizzesWithFilters(filter))
    }

    @GetMapping("/book/{bookId}/type/{type}")
    fun getQuizzesByBookIdAndType(
        @PathVariable bookId: Long,
        @PathVariable type: String
    ): ResponseEntity<List<QuizResponseDto>> {
        return ResponseEntity.ok(quizService.getQuizzesByBookIdAndType(bookId, type))
    }

    @PostMapping("/{quizId}/evaluate")
    fun evaluateQuizAnswer(
        @PathVariable quizId: Long,
        @RequestBody userAnswer: Any
    ): ResponseEntity<AnswerResult> {
        return ResponseEntity.ok(quizService.evaluateQuizAnswer(quizId, userAnswer))
    }
}