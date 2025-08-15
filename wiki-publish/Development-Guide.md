Development Guide

Code style

-   Kotlin with explicit, descriptive names; avoid abbreviations.
-   Keep functions small with early returns and clear guard clauses.

Pre-commit hook

-   Installed automatically on `./gradlew build` from `src/main/resources/git-hooks/pre-commit`.

Testing

-   Unit and slice tests under `src/test/kotlin` using JUnit 5.
-   Security and repository tests included; Testcontainers for PostgreSQL.

Running locally

-   Use `./gradlew bootRun`.
-   Ensure required environment variables are present (see Getting Started).

Troubleshooting builds

-   Clear Gradle cache: `./gradlew clean build`.
-   Verify Java 21 toolchain is used.
