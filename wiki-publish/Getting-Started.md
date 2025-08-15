Getting Started

Prerequisites

-   JDK 21
-   Docker (recommended for local DB via Compose or Testcontainers usage)
-   Gradle (wrapper provided)

Environment variables
Set the following before running locally:

-   `DB_URL` (e.g., `jdbc:postgresql://localhost:5432/bookquiz_db`)
-   `DB_USERNAME` (e.g., `postgres`)
-   `DB_PASSWORD` (e.g., `postgres`)
-   `JWT_SECRET` (use a sufficiently long random secret)
-   `JWT_EXPIRATION` (seconds; access token)
-   `JWT_REFRESH_EXPIRATION` (seconds; refresh token)

Run locally

```bash
./gradlew bootRun
```

Run tests

```bash
./gradlew test
```

Notes

-   Tests use JUnit 5 and Testcontainers for PostgreSQL. No manual DB is required for tests.
-   A pre-commit hook (`src/main/resources/git-hooks/pre-commit`) is installed on `./gradlew build`.

Build artifacts

```bash
./gradlew build
```

Build a container image

```bash
./gradlew bootBuildImage
```

The image name is configured as `jjoonleo/bookquiz-api` in `build.gradle.kts`.
