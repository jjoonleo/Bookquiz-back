Configuration

Spring properties (application.properties)

-   `spring.datasource.url` = `${DB_URL}`
-   `spring.datasource.username` = `${DB_USERNAME}`
-   `spring.datasource.password` = `${DB_PASSWORD}`
-   `spring.jpa.hibernate.ddl-auto` = `validate`
-   `spring.jpa.hibernate.naming.physical-strategy` = `org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl`
-   `spring.jpa.properties.hibernate.dialect` = `org.hibernate.dialect.PostgreSQLDialect`
-   `spring.jpa.show-sql` = `true`
-   `spring.jpa.properties.hibernate.format_sql` = `true`
-   `spring.jpa.open-in-view` = `false`
-   `spring.flyway.enabled` = `true`
-   `spring.flyway.locations` = `classpath:db/migration`
-   `jwt.secret` = `${JWT_SECRET}`
-   `jwt.expiration` = `${JWT_EXPIRATION}` (seconds)
-   `jwt.refresh-expiration` = `${JWT_REFRESH_EXPIRATION}` (seconds)

JWT configuration

-   Bound to `JwtProperties` (`jwt.secret`, `jwt.expiration`, `jwt.refresh-expiration`).
-   Tokens are HMAC-signed using the provided secret and validated by `JwtUtil`.

Security configuration

-   Stateless sessions and CSRF disabled for APIs.
-   Permitted paths: `/api/auth/**`, `/api/test/public`, `/actuator/**`, and `OPTIONS /**`.
-   All other endpoints require `Authorization: Bearer <token>`.

Actuator/observability

-   Actuator starter included; Prometheus registry on runtime classpath.
-   Ensure exposure settings as needed in your deployment environment.

Flyway

-   Migrations in `src/main/resources/db/migration` are auto-applied at startup.
-   A separate Flyway Gradle configuration also exists (see `build.gradle.kts`) targeting local development DB.
