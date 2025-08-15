Auth Endpoints (Detailed)

Base: `/api/auth`

General

-   All responses are wrapped in `ApiResponse<T>` with fields: `success: boolean`, `message: string`, `data: T | null`.
-   Unless noted, requests and responses are JSON.

Login

-   Method: POST
-   Path: `/api/auth/login`
-   Auth: Not required
-   Source: [AuthController.kt: login](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/AuthController.kt#L30-L39)
-   Request

```json
{
    "username": "string",
    "password": "string"
}
```

-   Validation
    -   `username`: required, max 50
    -   `password`: required
-   Response `data`

```json
{
    "accessToken": "<jwt>",
    "refreshToken": "<jwt>",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
        "username": "string",
        "name": "string",
        "email": "string",
        "authorities": ["ROLE_USER"]
    }
}
```

-   Authorities (roles)
    -   The `user.authorities` array contains role names.
    -   Possible values: `ROLE_USER`, `ROLE_ADMIN`.

Refresh Token

-   Method: POST
-   Path: `/api/auth/refresh`
-   Auth: Not required (uses refresh token in body)
-   Source: [AuthController.kt: refresh](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/AuthController.kt#L41-L50)
-   Request

```json
{ "refreshToken": "<refresh_jwt>" }
```

-   Validation
    -   `refreshToken`: required
-   Response `data`

```json
{
    "accessToken": "<jwt>",
    "tokenType": "Bearer",
    "expiresIn": 3600
}
```

Logout

-   Method: POST
-   Path: `/api/auth/logout`
-   Auth: Bearer token required
-   Source: [AuthController.kt: logout](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/AuthController.kt#L52-L61)
-   Request: empty body
-   Response

```json
{ "success": true, "message": "Logged out successfully", "data": null }
```

Get Profile

-   Method: GET
-   Path: `/api/auth/profile`
-   Auth: Bearer token required
-   Source: [AuthController.kt: getProfile](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/AuthController.kt#L116-L125)
-   Request: empty body
-   Response `data`

```json
{
    "username": "john_doe",
    "name": "John Doe",
    "email": "john@example.com",
    "phoneNumber": "+821012345678",
    "dateOfBirth": "1990-01-01T00:00:00",
    "province": "SEOUL",
    "grade": "GRADE_1",
    "gender": true,
    "lastLogin": "2024-01-01T10:00:00",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00",
    "authorities": ["ROLE_USER"]
}
```

Signup

-   Method: POST
-   Path: `/api/auth/signup`
-   Auth: Not required
-   Source: [AuthController.kt: signup](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/AuthController.kt#L63-L72)
-   Request

```json
{
    "username": "john_doe",
    "name": "John Doe",
    "password": "P@ssw0rd!",
    "confirmPassword": "P@ssw0rd!",
    "email": "john@example.com",
    "phoneNumber": "+821012345678",
    "dateOfBirth": "2024-01-01T00:00:00Z",
    "province": "SEOUL",
    "grade": "GRADE_1",
    "gender": true
}
```

-   Validation (high-level)
    -   `username`: required, 3–50, alphanumeric/underscore
    -   `name`: required, 2–64
    -   `password`: required, min 8, must include lowercase, uppercase, digit, special
    -   `confirmPassword`: required (must match `password`)
    -   `email`: required, valid email, max 255
    -   `phoneNumber`: required, E.164 format (e.g., `+8210...`)
    -   `dateOfBirth`: required (ISO-8601 string)
    -   `province`: required (enum)
    -   `grade`: required (enum)
    -   `gender`: required (boolean)
-   Response `data`

```json
{ "username": "john_doe", "name": "John Doe", "email": "john@example.com" }
```

Availability Checks

-   Method: GET
-   Paths and params:
    -   `/api/auth/check-username?username=john_doe`
    -   `/api/auth/check-email?email=john@example.com`
    -   `/api/auth/check-phone?phoneNumber=%2B821012345678`
-   Auth: Not required
-   Source: [AuthController.kt: checks](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/AuthController.kt#L74-L108)
-   Response `data`

```json
{ "available": true }
```

Headers

-   For protected endpoints (e.g., logout): `Authorization: Bearer <accessToken>`

Status Codes

-   200 OK: Success (login, refresh, logout, profile, checks)
-   201 Created: Signup success
-   400 Bad Request: Validation errors
-   401 Unauthorized: Invalid/missing token, invalid credentials

Examples (curl)

```bash
# Login
curl -sX POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"john","password":"P@ssw0rd!"}'

# Refresh
curl -sX POST http://localhost:8080/api/auth/refresh \
  -H 'Content-Type: application/json' \
  -d '{"refreshToken":"<refresh>"}'

# Logout
curl -sX POST http://localhost:8080/api/auth/logout \
  -H 'Authorization: Bearer <accessToken>'

# Get Profile
curl -sX GET http://localhost:8080/api/auth/profile \
  -H 'Authorization: Bearer <accessToken>'

# Signup
curl -sX POST http://localhost:8080/api/auth/signup \
  -H 'Content-Type: application/json' \
  -d '{"username":"john_doe","name":"John Doe","password":"P@ssw0rd!","confirmPassword":"P@ssw0rd!","email":"john@example.com","phoneNumber":"+821012345678","dateOfBirth":"2024-01-01T00:00:00Z","province":"SEOUL","grade":"GRADE_1","gender":true}'
```
