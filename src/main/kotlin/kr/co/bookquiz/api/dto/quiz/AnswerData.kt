package kr.co.bookquiz.api.dto.quiz

// For Multiple Choice
data class MultipleChoiceAnswerData(
        val options: List<String>,
        val correctIndex: Int,
        val selectedIndex: Int // user input
)

// For Subjective
data class SubjectiveAnswerData(
        val possibleAnswers: List<String>,
        val caseSensitive: Boolean = false,
        val maxWords: Int? = null,
        val answer: String // user input
)

// For True/False
data class TrueFalseAnswerData(
        val correctAnswer: Boolean,
        val answer: Boolean // user input
)

// Result DTO
data class AnswerResult(val isCorrect: Boolean, val score: Double, val feedback: String)
