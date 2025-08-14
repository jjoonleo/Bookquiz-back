# Quiz API Request Examples

This document provides JSON request body examples for creating different types of quizzes using the Book Quiz API.

## API Endpoint

All quiz creation requests should be sent to:

```
POST /api/quiz
```

## 1. Multiple Choice Quiz

### Request Body

```json
{
    "type": "MULTIPLE_CHOICE",
    "title": "What is the capital of France?",
    "explanation": "Paris has been the capital of France since 987 CE.",
    "hint": "Think of the city known for the Eiffel Tower.",
    "bookId": 1,
    "answer": 0,
    "options": ["Paris", "London", "Berlin", "Madrid"]
}
```

### Field Descriptions

-   `type`: Must be "MULTIPLE_CHOICE"
-   `title`: The quiz question text
-   `explanation`: Optional explanation shown after answering
-   `hint`: Optional hint to help users
-   `bookId`: ID of the book this quiz belongs to
-   `answer`: Index of the correct option (0-based)
-   `options`: Array of possible answers

## 2. Subjective Quiz

### Request Body

```json
{
    "type": "SUBJECTIVE",
    "title": "What is the main theme of the novel '1984'?",
    "explanation": "The novel explores themes of totalitarianism, surveillance, and the manipulation of truth.",
    "hint": "Consider how the government controls information and thought.",
    "bookId": 2,
    "answer": "Totalitarianism"
}
```

### Field Descriptions

-   `type`: Must be "SUBJECTIVE"
-   `title`: The quiz question text
-   `explanation`: Optional explanation shown after answering
-   `hint`: Optional hint to help users
-   `bookId`: ID of the book this quiz belongs to
-   `answer`: The correct answer text

## 3. True/False Quiz

### Request Body

```json
{
    "type": "TRUE_FALSE",
    "title": "The Great Gatsby was written by F. Scott Fitzgerald.",
    "explanation": "F. Scott Fitzgerald published The Great Gatsby in 1925.",
    "hint": "The author's initials are F.S.F.",
    "bookId": 3,
    "answer": true
}
```

### Field Descriptions

-   `type`: Must be "TRUE_FALSE"
-   `title`: The quiz question text
-   `explanation`: Optional explanation shown after answering
-   `hint`: Optional hint to help users
-   `bookId`: ID of the book this quiz belongs to
-   `answer`: Boolean value (true or false)

## Complete Examples with All Optional Fields

### Multiple Choice Quiz (Complete)

```json
{
    "type": "MULTIPLE_CHOICE",
    "title": "Which character dies at the end of 'Romeo and Juliet'?",
    "explanation": "Both Romeo and Juliet die at the end of the play due to a series of misunderstandings.",
    "hint": "Both main characters meet the same fate.",
    "bookId": 4,
    "answer": 3,
    "options": [
        "Only Romeo",
        "Only Juliet",
        "Neither Romeo nor Juliet",
        "Both Romeo and Juliet"
    ]
}
```

### Subjective Quiz (Complete)

```json
{
    "type": "SUBJECTIVE",
    "title": "What year was 'Pride and Prejudice' first published?",
    "explanation": "Jane Austen's 'Pride and Prejudice' was first published in 1813.",
    "hint": "It was published in the early 19th century.",
    "bookId": 5,
    "answer": "1813"
}
```

### True/False Quiz (Complete)

```json
{
    "type": "TRUE_FALSE",
    "title": "Shakespeare wrote 'Hamlet' in the 17th century.",
    "explanation": "Shakespeare wrote 'Hamlet' between 1599 and 1601, which is in the late 16th century.",
    "hint": "Shakespeare lived from 1564 to 1616.",
    "bookId": 6,
    "answer": false
}
```

## Minimal Examples (Only Required Fields)

### Multiple Choice Quiz (Minimal)

```json
{
    "type": "MULTIPLE_CHOICE",
    "title": "What is 2 + 2?",
    "bookId": 1,
    "answer": 0,
    "options": ["4", "5", "6", "7"]
}
```

### Subjective Quiz (Minimal)

```json
{
    "type": "SUBJECTIVE",
    "title": "What is the color of the sky?",
    "bookId": 1,
    "answer": "blue"
}
```

### True/False Quiz (Minimal)

```json
{
    "type": "TRUE_FALSE",
    "title": "The Earth is round.",
    "bookId": 1,
    "answer": true
}
```

## Response Format

All successful quiz creation requests will return a response similar to:

```json
{
    "id": 1,
    "type": "MULTIPLE_CHOICE",
    "title": "What is the capital of France?",
    "explanation": "Paris has been the capital of France since 987 CE.",
    "hint": "Think of the city known for the Eiffel Tower.",
    "bookId": 1,
    "createdAt": "2024-01-15T10:30:00",
    "answer": 0,
    "options": ["Paris", "London", "Berlin", "Madrid"]
}
```

## Error Responses

### Invalid Quiz Type

```json
{
    "success": false,
    "message": "Unknown quiz type: INVALID_TYPE"
}
```

### Missing Required Fields

```json
{
    "success": false,
    "message": "Title is required"
}
```

### Invalid Answer Index (Multiple Choice)

```json
{
    "success": false,
    "message": "Answer index must be within the range of options"
}
```

## Notes

1. **Answer Index for Multiple Choice**: The `answer` field uses 0-based indexing. For example, if the correct answer is the first option, use `0`.

2. **Optional Fields**: `explanation` and `hint` are optional and can be omitted or set to `null`.

3. **Book ID**: The `bookId` must reference an existing book in the system.

4. **Case Sensitivity**: For subjective quizzes, the answer matching is case-insensitive by default.

5. **Validation**: The API will validate that:
    - All required fields are present
    - The book ID exists
    - For multiple choice: answer index is within the options range
    - For multiple choice: at least 2 options are provided
