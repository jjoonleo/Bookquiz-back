Testing and QA

Test framework

-   JUnit 5 with Spring Boot test starters.
-   Testcontainers for PostgreSQL and Spring Boot Testcontainers integration.

Where tests live

-   `src/test/kotlin/kr/co/bookquiz/api` with controller, service, repository, and integration tests.

Run tests

```bash
./gradlew test
```

What to test

-   Service logic, strategy correctness, repository queries, controller contracts.
-   Security configuration via `spring-security-test`.

CI recommendations

-   Run `./gradlew build` on pull requests.
-   Optionally run database container for integration tests if not using Testcontainers.
