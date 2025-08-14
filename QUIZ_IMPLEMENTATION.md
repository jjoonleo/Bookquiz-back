# Book Quiz Implementation

This document describes the implementation of a comprehensive Book Quiz system using **table-per-subclass inheritance** and the **strategy pattern** in Kotlin/Spring Boot.

## Architecture Overview

The system is designed with the following key principles:

-   **Table-per-subclass inheritance** for different answer types
-   **Strategy pattern** for answer evaluation logic
-   **Strongly-typed entities** - no JSON blobs or string parsing
-   **Unified strategy interface** - all methods take the same type for correct and user answers
-   **Type-safe operations** throughout the application

## Entity Structure

### 1. Quiz Entity

```kotlin
@Entity
@Table(name = "quizzes")
data class Quiz(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val title: String,
    val questionType: String, // "MULTIPLE_CHOICE", "SUBJECTIVE", "TRUE_FALSE"
    val points: Int = 1,
    val explanation: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
```

### 2. Answer Entity Hierarchy (Table-per-Subclass)

#### Base AnswerEntity

```kotlin
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "answers")
abstract class AnswerEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false, unique = true)
    open val quiz: Quiz,
    open val createdAt: LocalDateTime = LocalDateTime.now()
)
```

#### Multiple Choice Answer

```kotlin
@Entity
@Table(name = "multiple_choice_answers")
@PrimaryKeyJoinColumn(name = "id")
class MultipleChoiceAnswerEntity(
    val correctIndex: Int,
    @ElementCollection
    @CollectionTable(name = "multiple_choice_options")
    val options: List<String> = emptyList(),
    // ... inherits from AnswerEntity
)
```

#### Subjective Answer

```kotlin
@Entity
@Table(name = "subjective_answers")
@PrimaryKeyJoinColumn(name = "id")
class SubjectiveAnswerEntity(
    @ElementCollection
    @CollectionTable(name = "subjective_possible_answers")
    val possibleAnswers: List<String> = emptyList(),
    val caseSensitive: Boolean = false,
    val maxWords: Int? = null,
    // ... inherits from AnswerEntity
)
```

#### True/False Answer

```kotlin
@Entity
@Table(name = "true_false_answers")
@PrimaryKeyJoinColumn(name = "id")
class TrueFalseAnswerEntity(
    val correctAnswer: Boolean,
    // ... inherits from AnswerEntity
)
```

## Data Transfer Objects (DTOs)

These are **NOT entities** but used for passing data between layers:

```kotlin
// Quiz Response DTO
data class QuizResponseDto(
    val id: Long,
    val title: String,
    val questionType: String,
    val points: Int,
    val explanation: String?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(quiz: Quiz): QuizResponseDto {
            return QuizResponseDto(
                id = quiz.id,
                title = quiz.title,
                questionType = quiz.questionType,
                points = quiz.points,
                explanation = quiz.explanation,
                createdAt = quiz.createdAt
            )
        }
    }
}

// Multiple Choice
data class MultipleChoiceAnswerData(
    val options: List<String>,
    val correctIndex: Int,
    val selectedIndex: Int // user input
)

// Subjective
data class SubjectiveAnswerData(
    val possibleAnswers: List<String>,
    val caseSensitive: Boolean = false,
    val maxWords: Int? = null,
    val answer: String // user input
)

// True/False
data class TrueFalseAnswerData(
    val correctAnswer: Boolean,
    val answer: Boolean // user input
)

// Result
data class AnswerResult(
    val isCorrect: Boolean,
    val score: Double,
    val feedback: String
)
```

## Strategy Pattern Implementation

### Generic Strategy Interface

```kotlin
interface AnswerStrategy<T> {
    fun validate(correct: T, user: T): Boolean
    fun isCorrect(correct: T, user: T): Boolean
    fun score(correct: T, user: T): Double
    fun feedback(correct: T, user: T, score: Double): String
}
```

### Strategy Implementations

#### Multiple Choice Strategy

-   Validates that selected index is within bounds
-   Checks if selected index matches correct index
-   Provides appropriate feedback

#### Subjective Strategy

-   Validates word count limits
-   Supports case-sensitive and case-insensitive matching
-   Checks against multiple possible answers

#### True/False Strategy

-   Simple boolean comparison
-   Always valid (no validation needed)

### Strategy Registry

```kotlin
@Component
class AnswerStrategyRegistry(
    private val mc: MultipleChoiceStrategy,
    private val subj: SubjectiveStrategy,
    private val tf: TrueFalseStrategy
) {
    fun <T> getStrategy(type: String): AnswerStrategy<T> = when (type) {
        "MULTIPLE_CHOICE" -> mc as AnswerStrategy<T>
        "SUBJECTIVE" -> subj as AnswerStrategy<T>
        "TRUE_FALSE" -> tf as AnswerStrategy<T>
        else -> throw IllegalArgumentException("Unknown question type: $type")
    }
}
```

## Service Layer

The `QuizService` provides:

-   Dynamic strategy dispatch based on `questionType`
-   Type-safe answer evaluation
-   Comprehensive error handling
-   Transactional operations

```kotlin
@Service
class QuizService(
    private val quizRepo: QuizRepository,
    private val mcRepo: MultipleChoiceAnswerRepository,
    private val subjRepo: SubjectiveAnswerRepository,
    private val tfRepo: TrueFalseAnswerRepository,
    private val registry: AnswerStrategyRegistry
) {
    @Transactional(readOnly = true)
    fun evaluateQuizAnswer(quizId: Long, userAnswer: Any): AnswerResult {
        // Dynamic dispatch based on question type
        // Type-safe evaluation using appropriate strategy
    }
}
```

## API Endpoints

### Quiz Information

-   `GET /api/quiz/{quizId}` - Get quiz details
-   `GET /api/quiz/book/{bookId}` - Get all quizzes for a specific book
-   `GET /api/quiz/{quizId}/answer/multiple-choice` - Get MCQ answer
-   `GET /api/quiz/{quizId}/answer/subjective` - Get subjective answer
-   `GET /api/quiz/{quizId}/answer/true-false` - Get True/False answer

### Answer Evaluation

-   `POST /api/quiz/{quizId}/evaluate/multiple-choice` - Evaluate MCQ answer
-   `POST /api/quiz/{quizId}/evaluate/subjective` - Evaluate subjective answer
-   `POST /api/quiz/{quizId}/evaluate/true-false` - Evaluate True/False answer

## Database Schema

The implementation uses **table-per-subclass inheritance** with the following tables:

```sql
-- Base quiz table
CREATE TABLE quizzes (
    id SERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    question_type VARCHAR(32) NOT NULL,
    points INT NOT NULL DEFAULT 1,
    explanation TEXT,
    book_id INTEGER NOT NULL REFERENCES books(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Base answer table
CREATE TABLE answers (
    id SERIAL PRIMARY KEY,
    quiz_id INTEGER UNIQUE NOT NULL REFERENCES quizzes(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Multiple choice specific table
CREATE TABLE multiple_choice_answers (
    id INTEGER PRIMARY KEY REFERENCES answers(id),
    correct_index INT NOT NULL
);

CREATE TABLE multiple_choice_options (
    answer_id INTEGER REFERENCES multiple_choice_answers(id),
    option_text TEXT NOT NULL
);

-- Subjective specific table
CREATE TABLE subjective_answers (
    id INTEGER PRIMARY KEY REFERENCES answers(id),
    case_sensitive BOOLEAN DEFAULT FALSE NOT NULL,
    max_words INT
);

CREATE TABLE subjective_possible_answers (
    answer_id INTEGER REFERENCES subjective_answers(id),
    possible_answer TEXT NOT NULL
);

-- True/False specific table
CREATE TABLE true_false_answers (
    id INTEGER PRIMARY KEY REFERENCES answers(id),
    correct_answer BOOLEAN NOT NULL
);
```

## Key Benefits

1. **Type Safety**: All operations are strongly typed with no JSON blobs or string parsing
2. **Extensibility**: Easy to add new question types by implementing the strategy pattern
3. **Normalized Data**: Proper database normalization with table-per-subclass inheritance
4. **Unified Interface**: All strategies implement the same interface for consistency
5. **Separation of Concerns**: Clear separation between entities, DTOs, and business logic
6. **Testability**: Comprehensive unit tests with mocked dependencies

## Usage Examples

### Creating a Multiple Choice Quiz

```kotlin
val book = Book(
    id = 1L,
    title = "Geography Book",
    isbn = "1234567890123",
    publisher = "Test Publisher",
    quizPrice = 1000
)

val quiz = Quiz(
    title = "What is the capital of France?",
    questionType = "MULTIPLE_CHOICE",
    points = 5,
    book = book
)

val mcAnswer = MultipleChoiceAnswerEntity(
    correctIndex = 1,
    options = listOf("London", "Paris", "Berlin", "Madrid"),
    quiz = quiz
)
```

### Evaluating a User Answer

```kotlin
val userAnswer = MultipleChoiceAnswerData(
    options = listOf("London", "Paris", "Berlin", "Madrid"),
    correctIndex = 1,
    selectedIndex = 1
)

val result = quizService.evaluateQuizAnswer(quizId, userAnswer)
// Returns: AnswerResult(isCorrect = true, score = 1.0, feedback = "Correct!")
```

## Testing

The implementation includes comprehensive unit tests covering:

-   Correct and incorrect answer evaluation
-   Different question types
-   Error handling for unknown question types
-   Error handling for non-existent quizzes
-   Strategy pattern validation

## Future Extensions

The architecture supports easy extension for new question types:

1. Create new entity extending `AnswerEntity`
2. Create corresponding data class
3. Implement new strategy
4. Add to strategy registry
5. Update service layer

This design ensures the system remains maintainable and extensible while providing type safety and proper separation of concerns.
