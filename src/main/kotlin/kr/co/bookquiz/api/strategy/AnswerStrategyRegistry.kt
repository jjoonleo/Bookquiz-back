package kr.co.bookquiz.api.strategy

import org.springframework.stereotype.Component

@Component
class AnswerStrategyRegistry(
        private val mc: MultipleChoiceStrategy,
        private val subj: SubjectiveStrategy,
        private val tf: TrueFalseStrategy
) {
  fun <T> getStrategy(type: String): AnswerStrategy<T> =
          when (type) {
            "MULTIPLE_CHOICE" -> mc as AnswerStrategy<T>
            "SUBJECTIVE" -> subj as AnswerStrategy<T>
            "TRUE_FALSE" -> tf as AnswerStrategy<T>
            else -> throw IllegalArgumentException("Unknown question type: $type")
          }
}
