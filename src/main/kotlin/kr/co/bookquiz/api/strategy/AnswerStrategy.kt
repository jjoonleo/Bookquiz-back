package kr.co.bookquiz.api.strategy

interface AnswerStrategy<T> {
  fun validate(correct: T, user: T): Boolean
  fun isCorrect(correct: T, user: T): Boolean
  fun score(correct: T, user: T): Double
  fun feedback(correct: T, user: T, score: Double): String
}
