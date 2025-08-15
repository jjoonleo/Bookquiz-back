Books Endpoints (Detailed)

Base: `/api/books`

General

-   Responses wrap payloads in `ApiResponse<T>`.
-   Requires Bearer token unless your security allows otherwise (see Configuration/Security).

Create Book

-   Method: POST `/api/books`
-   Source: [BookController.kt: create](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/BookController.kt#L23-L29)
-   Request

```json
{
    "title": "string (<=255)",
    "subtitle": "string|null (<=500)",
    "isbn": "string (<=30)",
    "publisher": "string (<=255)",
    "quizPrice": 1000,
    "maxAttempt": 3,
    "thumbnail": "https://...",
    "authors": "comma-separated or text (<=2000)",
    "translators": "string (<=2000)",
    "illustrators": "string (<=2000)"
}
```

-   Response `data`

```json
{
    "id": 1,
    "title": "string",
    "subtitle": "string|null",
    "isbn": "string",
    "publisher": "string",
    "quizPrice": 1000,
    "maxAttempt": 3,
    "thumbnail": "https://...",
    "authors": "string",
    "translators": "string",
    "illustrators": "string"
}
```

Create Books (Bulk)

-   Method: POST `/api/books/bulk`
-   Source: [BookController.kt: create bulk](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/BookController.kt#L31-L37)
-   Request: array of the Create Book request objects
-   Response `data`: array of Book responses

Get Book

-   Method: GET `/api/books/{id}`
-   Source: [BookController.kt: get](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/BookController.kt#L39-L43)
-   Response `data`: Book response

Update Book

-   Method: PUT `/api/books/{id}`
-   Source: [BookController.kt: update](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/BookController.kt#L45-L52)
-   Request: same shape as Create Book
-   Response `data`: Book response

Delete Book

-   Method: DELETE `/api/books/{id}`
-   Source: [BookController.kt: delete](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/BookController.kt#L54-L58)
-   Response `data`: null

Validation

-   Title, ISBN, Publisher, Authors are required
-   `quizPrice` positive; `maxAttempt` positive
-   Field length limits as noted above
