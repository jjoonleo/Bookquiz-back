Database and Migrations

Database

-   PostgreSQL is the primary database.
-   JPA/Hibernate with `ddl-auto=validate` to enforce schema matches.

Migrations

-   Flyway runs at startup from `classpath:db/migration`.
-   Existing migrations: `V1_0_0__init_enum.sql`, `V1_0_1__init.sql`, `V1_0_2__insert_default_authorities.sql`, `V1_0_3__books_people_to_strings.sql`, `V1_0_4__add_subtitle_to_books.sql`.

Local Flyway (Gradle)
Configured in `build.gradle.kts`:

-   `flyway.url=jdbc:postgresql://localhost:5432/bookquiz_db`
-   `flyway.user=postgres`
-   `flyway.password=postgres`

Common tasks

-   Add a new migration as `V<major>_<minor>_<patch>__<description>.sql`.
-   Keep entity changes and migrations in the same PR to avoid drift.
