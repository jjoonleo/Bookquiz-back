User Answers Endpoints (Detailed)

Base: `/api/user-answers`

Create (Submit) User Answer

-   Method: POST `/api/user-answers`
-   Auth: Bearer token; uses authenticated principal username
-   Source: [UserAnswerController.kt: submit](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/UserAnswerController.kt#L15-L30)
-   Request (polymorphic by `type`)

```json
// MULTIPLE_CHOICE
{
  "type": "MULTIPLE_CHOICE",
  "quizId": 1,
  "userAnswer": 2,
  "attemptNumber": 1
}

// SUBJECTIVE
{
  "type": "SUBJECTIVE",
  "quizId": 1,
  "userAnswer": "text",
  "attemptNumber": 1
}

// TRUE_FALSE
{
  "type": "TRUE_FALSE",
  "quizId": 1,
  "userAnswer": true,
  "attemptNumber": 1
}
```

-   Response `data` (by type)

```json
{
    "id": 10,
    "type": "MULTIPLE_CHOICE",
    "username": "john",
    "quizId": 1,
    "attemptNumber": 1,
    "isCorrect": true,
    "answeredAt": "2024-01-01T00:00:00",
    "userAnswer": 2
}
```

Update User Answer

-   Method: PUT `/api/user-answers/{id}`
-   Source: [UserAnswerController.kt: update](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/UserAnswerController.kt#L32-L46)
-   Request: same `type` discriminator and updatable fields (`userAnswer`, `attemptNumber`, `isCorrect`)
-   Response `data`: Updated UserAnswer response

Get Current User's Answers by Quiz

-   Method: GET `/api/user-answers/quiz/{quizId}`
-   Source: [UserAnswerController.kt: list by quiz](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/UserAnswerController.kt#L48-L63)
-   Response `data`: List of UserAnswer responses for the authenticated user
