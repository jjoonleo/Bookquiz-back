Troubleshooting

Build fails with Java version errors

-   Ensure Java 21 is installed and the Gradle toolchain resolves to 21.

DB connection failures on startup

-   Verify `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` env vars.
-   Ensure PostgreSQL is reachable; for local dev use Docker or Testcontainers.

JWT errors (invalid/expired)

-   Check `JWT_SECRET` length and consistency across instances.
-   Confirm token in `Authorization` header begins with `Bearer `.

Flyway migration errors

-   Ensure schema matches entities; use `ddl-auto=validate` to detect drift.
-   Apply pending migrations or fix failed checksums.

403/401 on protected endpoints

-   Acquire access token via `/api/auth/login` and include in header.
-   Preflight (CORS) should be allowed; check client sends `OPTIONS` correctly.
