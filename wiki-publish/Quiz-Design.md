Quiz Design and Domain Model

Overview

-   Strongly-typed quiz and answer modeling with table-per-subclass inheritance and strategy pattern.
-   No JSON blobs in entities; DTOs used for API boundaries.

Entities

-   `Quiz` — core quiz metadata and type.
-   `AnswerEntity` (base) → `MultipleChoiceAnswerEntity`, `SubjectiveAnswerEntity`, `TrueFalseAnswerEntity`.

DTOs

-   `QuizResponseDto`, `CreateQuizDto`, `UpdateQuizDto`
-   Answer data DTOs used for evaluation

Evaluation flow

1. Client submits user answer to `POST /api/quiz/{quizId}/evaluate`.
2. `QuizService` loads quiz and correct answer entity.
3. Strategy resolved by quiz type evaluates correctness, score, and feedback.

Further reading

-   See `QUIZ_IMPLEMENTATION.md` in the repo for code-level details and examples.
