# Bookquiz-back

Kotlin/Spring Boot backend for a book quiz service with authentication, books,
quizzes, user answers, and Toss payment confirmation.

## Why This Project Matters

This repository demonstrates backend API design around real product domains:
users authenticate with JWT, books and quizzes are managed through REST APIs,
answers can be evaluated, and payments are confirmed through a dedicated payment
controller.

## Features

- JWT authentication and refresh flow
- Signup/login/profile APIs
- Book CRUD APIs
- Quiz CRUD, filtering, search, and answer evaluation
- User answer submission and update APIs
- Toss payment confirmation endpoint
- PostgreSQL schema migrations with Flyway
- Global exception handling and typed API responses
- Integration and repository testing with Testcontainers
- Wiki-style documentation under `wiki-publish/`

## Tech Stack

- Kotlin
- Spring Boot 3
- Spring Security
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway
- JWT
- JUnit 5
- Mockito
- Testcontainers
- Gradle

## Local Development

Required environment variables:

```bash
DB_URL=jdbc:postgresql://localhost:5432/bookquiz_db
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=replace-with-a-long-random-secret
JWT_EXPIRATION=3600
JWT_REFRESH_EXPIRATION=86400
```

Run locally:

```bash
./gradlew bootRun
```

Run tests:

```bash
./gradlew test
```

Build:

```bash
./gradlew build
```

## Documentation

Detailed documentation is available in `wiki-publish/`, including:

- Getting started
- Authentication and authorization
- API reference
- Quiz design
- Database and migrations
- Deployment
- Testing and QA

## Portfolio Highlight

The strongest point is backend completeness: authentication, domain APIs,
database migrations, payment integration, and Testcontainers-based validation
are all present in one Kotlin/Spring project.
