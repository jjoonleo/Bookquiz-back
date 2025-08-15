Bookquiz-back Wiki

Welcome to the Bookquiz-back project wiki. This backend is built with Kotlin and Spring Boot 3, providing quiz, book, authentication, payment, and user-answer APIs.

Quick links

-   Getting started: [[Getting-Started]]
-   Configuration: [[Configuration]]
-   Authentication and authorization: [[Authentication-and-Authorization]]
-   API reference: [[API-Reference]]
-   Quiz design and domain model: [[Quiz-Design]]
-   Database and migrations: [[Database-and-Migrations]]
-   Development guide: [[Development-Guide]]
-   Deployment: [[Deployment]]
-   Testing and QA: [[Testing-and-QA]]
-   Troubleshooting: [[Troubleshooting]]

Repository structure (high-level)

-   `src/main/kotlin/kr/co/bookquiz/api`: Application code (controllers, services, security, entities, repositories)
-   `src/main/resources`: Spring configuration, Flyway migrations, templates/static
-   `deployment/docker-compose`: Compose files for infra and app
-   `build.gradle.kts`: Build, testing, and Docker image config

Tech stack

-   Kotlin 1.9, Spring Boot 3.5, Spring Security, JPA/Hibernate
-   PostgreSQL, Flyway, Testcontainers
-   JWT (io.jsonwebtoken)
-   Actuator + Prometheus registry
