Authentication and Authorization

Overview

-   JWT-based stateless authentication using Spring Security.
-   Login issues short-lived access token; refresh endpoint issues a new pair when provided a valid refresh token.

Headers

-   `Authorization: Bearer <access_token>` on all protected endpoints.

Public vs protected

-   Public: `/api/auth/**`, `/api/test/public`, `/actuator/**`, and `OPTIONS /**`.
-   Protected: everything else.

Auth endpoints

-   `POST /api/auth/login` — Authenticate with credentials; returns tokens.
-   `POST /api/auth/refresh` — Exchange refresh token for new access/refresh pair.
-   `POST /api/auth/logout` — Invalidate current session context on server side (client should discard tokens).
-   `POST /api/auth/signup` — Register a new user.
-   `GET /api/auth/check-username?username=...` — Check username availability.
-   `GET /api/auth/check-email?email=...` — Check email availability.
-   `GET /api/auth/check-phone?phoneNumber=...` — Check phone availability.

Token lifetimes

-   `jwt.expiration` (seconds) for access tokens.
-   `jwt.refresh-expiration` (seconds) for refresh tokens.

User roles (authorities)

-   Default roles: `ROLE_USER`, `ROLE_ADMIN`.
-   Seeded via Flyway: [`V1_0_2__insert_default_authorities.sql`](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/resources/db/migration/V1_0_2__insert_default_authorities.sql)
-   Returned in auth responses as `user.authorities`.

Common errors

-   Expired or malformed token → 401 with error details.
-   Missing `Authorization` header on protected routes → 401.
