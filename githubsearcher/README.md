# GitHub Repository Searcher

A Spring Boot REST API that searches GitHub repositories using the GitHub REST API, stores repository information in PostgreSQL, and provides filtering and sorting capabilities.

## Tech Stack

- Java 24
- Spring Boot 4
- Spring Data JPA
- PostgreSQL
- GitHub REST API
- Maven
- Lombok
- JUnit 5 and Mockito
- Swagger / OpenAPI

## Features

- Search GitHub repositories by keyword and programming language.
- Sort search results by stars, forks, or last updated date.
- Store repository information in PostgreSQL.
- Update existing repositories instead of creating duplicates.
- Retrieve saved repositories.
- Filter repositories by language and minimum stars.
- Handle GitHub API errors and rate limits.
- Validate incoming API requests.

## API Endpoints

### 1. Search GitHub repositories

`POST /api/github/search`

Example request:

```json
{
  "query": "spring boot",
  "language": "Java",
  "sort": "stars"
}
```

### 2. Get saved repositories

`GET /api/github/repositories`

Optional query parameters:

- `language` — filter by programming language.
- `minStars` — filter by minimum stars.
- `sort` — `stars`, `forks`, or `updated`.

Example:

`GET /api/github/repositories?language=Java&minStars=1000&sort=stars`

## Configuration

Configure the following environment variables:

| Variable | Description |
|---|---|
| `DB_URL` | PostgreSQL JDBC connection URL |
| `DB_USERNAME` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |
| `GITHUB_TOKEN` | Optional GitHub personal access token |

The GitHub token is optional, but using one provides a higher API rate limit.

## Run Locally

1. Clone the repository.
2. Configure the required environment variables.
3. Ensure PostgreSQL is accessible.
4. Run the application:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The application runs at:

`http://localhost:8080`

## Swagger Documentation

Once the application is running, access:

`http://localhost:8080/swagger-ui/index.html`

## Running Tests

Run the test suite using:

```bash
./mvnw test
```

On Windows:

```bash
mvnw.cmd test
```