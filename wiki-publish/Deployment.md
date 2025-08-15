Deployment

Container image

-   Build with `./gradlew bootBuildImage` → image name `jjoonleo/bookquiz-api`.
-   Builder: `dashaun/builder:tiny` (as configured).

Docker Compose

-   See `deployment/docker-compose/infra.yml` (infrastructure) and `deployment/docker-compose/apps.yml` (services) for a suggested stack.

Environment configuration

-   Provide DB and JWT environment variables (see Configuration page).
-   Expose Actuator endpoints as needed and secure appropriately.

Database migrations

-   Flyway runs automatically at startup.

Health and metrics

-   Spring Boot Actuator exposes health and metrics; add Prometheus scrape if applicable.
