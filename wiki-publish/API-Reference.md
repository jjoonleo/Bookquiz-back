API Reference

Conventions

-   Base path: `/api`
-   All JSON unless specified.
-   Protected endpoints require `Authorization: Bearer <token>`.

Auth

-   `POST /api/auth/login`
-   `POST /api/auth/refresh`
-   `POST /api/auth/logout`
-   `POST /api/auth/signup`
-   `GET /api/auth/check-username?username=...`
-   `GET /api/auth/check-email?email=...`
-   `GET /api/auth/check-phone?phoneNumber=...`

Books

-   `POST /api/books` — Create book
-   `POST /api/books/bulk` — Create multiple books
-   `GET /api/books/{id}` — Get book by id
-   `PUT /api/books/{id}` — Update book
-   `DELETE /api/books/{id}` — Delete book

Quizzes

-   `POST /api/quiz` — Create quiz
-   `GET /api/quiz/{quizId}` — Get quiz
-   `PUT /api/quiz/{quizId}` — Update quiz
-   `DELETE /api/quiz/{quizId}` — Delete quiz
-   `GET /api/quiz/book/{bookId}` — List quizzes by book
-   `GET /api/quiz/search?title=...` — Search by title
-   `GET /api/quiz/filter` — Filter using `QuizFilterDto` query params
-   `GET /api/quiz/book/{bookId}/type/{type}` — List by book and type
-   `POST /api/quiz/{quizId}/evaluate` — Evaluate user answer

User Answers

-   `POST /api/user-answers` — Submit an answer (auth user)
-   `PUT /api/user-answers/{id}` — Update an answer
-   `GET /api/user-answers/quiz/{quizId}` — Get current user's answers for a quiz

Payments

-   `POST /api/payments/confirm` — Confirm payment (Toss)

Test utilities

-   `GET /api/test/public`
-   `GET /api/test/protected`

See also

-   Quiz request/response examples: `QUIZ_API_EXAMPLES.md`
