Quizzes Endpoints (Detailed)

Base: `/api/quiz`

Polymorphic payloads

-   Use `type` discriminator: `MULTIPLE_CHOICE`, `SUBJECTIVE`, `TRUE_FALSE`.

Create Quiz

-   Method: POST `/api/quiz`
-   Source: [QuizController.kt: create](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/QuizController.kt#L25-L28)
-   Request (by type)

```json
// MULTIPLE_CHOICE
{
  "type": "MULTIPLE_CHOICE",
  "title": "string",
  "explanation": "string|null",
  "hint": "string|null",
  "bookId": 1,
  "answer": 0,
  "options": ["A","B","C"]
}

// SUBJECTIVE
{
  "type": "SUBJECTIVE",
  "title": "string",
  "explanation": "string|null",
  "hint": "string|null",
  "bookId": 1,
  "answer": "text"
}

// TRUE_FALSE
{
  "type": "TRUE_FALSE",
  "title": "string",
  "explanation": "string|null",
  "hint": "string|null",
  "bookId": 1,
  "answer": true
}
```

-   Response `data` (by type)

```json
// MULTIPLE_CHOICE
{
    "id": 1,
    "type": "MULTIPLE_CHOICE",
    "title": "string",
    "explanation": "string|null",
    "hint": "string|null",
    "bookId": 1,
    "createdAt": "2024-01-01T00:00:00",
    "answer": 0,
    "options": ["A", "B", "C"]
}
```

Get Quiz

-   Method: GET `/api/quiz/{quizId}`
-   Source: [QuizController.kt: get](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/QuizController.kt#L30-L33)
-   Response `data`: Quiz response (by type)

Update Quiz

-   Method: PUT `/api/quiz/{quizId}`
-   Source: [QuizController.kt: update](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/QuizController.kt#L35-L41)
-   Request: same discriminator with partial fields per type
-   Response `data`: Quiz response (by type)

Delete Quiz

-   Method: DELETE `/api/quiz/{quizId}`
-   Source: [QuizController.kt: delete](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/QuizController.kt#L43-L47)
-   Response: 200 OK (empty body)

List by Book

-   Method: GET `/api/quiz/book/{bookId}`
-   Source: [QuizController.kt: list by book](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/QuizController.kt#L49-L52)
-   Response `data`: List of Quiz responses

Search by Title

-   Method: GET `/api/quiz/search?title=...`
-   Source: [QuizController.kt: search](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/QuizController.kt#L54-L57)
-   Response `data`: List of Quiz responses

Filter

-   Method: GET `/api/quiz/filter`
-   Source: [QuizController.kt: filter](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/QuizController.kt#L59-L64)
-   Query params: `type`, `bookId`, `title`
-   Response `data`: List of Quiz responses

List by Book and Type

-   Method: GET `/api/quiz/book/{bookId}/type/{type}`
-   Source: [QuizController.kt: list by book and type](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/QuizController.kt#L66-L72)
-   Response `data`: List of Quiz responses

Evaluate Answer

-   Method: POST `/api/quiz/{quizId}/evaluate`
-   Source: [QuizController.kt: evaluate](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/QuizController.kt#L74-L80)
-   Request: user answer payload matching quiz type
    -   MULTIPLE_CHOICE: `{ "selectedIndex": number }` or as defined by service layer
    -   SUBJECTIVE: `{ "answer": "string" }`
    -   TRUE_FALSE: `{ "answer": true }`
-   Response `data` (example)

```json
{ "isCorrect": true, "score": 1.0, "feedback": "Correct!" }
```

See also

-   `QUIZ_API_EXAMPLES.md` in repo for full examples
-   [[Quiz-Design]] for modeling
