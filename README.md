## Tech Stack

- Java 17 Spring Boot 4.0.6
- Spring Security (stateless JWT)
- Spring Data JPA + MySQL 8.0
- BCrypt for password hashing
- Docker Docker Compose

---

## Setup Instructions

### Prerequisites

- Docker and Docker Compose installed
- Java 17+ and Maven (if running locally without Docker)

### Environment Variables

Create a `.env` file in the project root:

```env
DB_ROOT_PASSWORD=yourRootPassword
SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/prj_management
DB_USERNAME=root
DB_PASSWORD=yourRootPassword
JWT_SECRET=bXlTdXBlclNlY3JldEtleUZvckpXVEF1dGhlbnRpY2F0aW9u (must be a Base64 URL-encoded string of at least 32 bytes.)
JWT_EXPIRATION=86400000
```

### Run with Docker

```bash
docker compose up --build
```

The API will be available at `http://localhost:8080`.

### Run Locally (without Docker)

1. Start a local MySQL 8.0 instance and create a database named `prj_management`.
2. Update `src/main/resources/application.properties` with your local DB credentials.
3. Run:

```bash
./mvnw spring-boot:run
```

---

## Database Schema

Hibernate auto-generates tables from the JPA entities (`spring.jpa.hibernate.ddl-auto=update`). The schema consists of seven tables:

```
users               — id, email (unique), password_hash, name
projects            — id, name, owner_id (FK → users), created_at
project_members     — id, project_id (FK), user_id (FK), role (OWNER|MEMBER)
tasks               — id, project_id (FK), title, description, status(TODO|IN_PROGRESS|DONE), created_at, updated_at
task_assignees      — id, task_id (FK), user_id (FK)
task_events         — id, task_id (FK), event_type, payload, created_at
token_blacklist     — id, user_id (FK), token (unique), expires_at, revoked_at
```

---

## Postman collections

https://hieu1211-3949030.postman.co/workspace/Personal-Workspace~f73dbee6-e451-45d5-b2fb-ddbff4d075d0/collection/47032019-6fed8ec8-cef4-44b4-90b6-9936b0819679?action=share&creator=47032019

## API Documentation

All authenticated endpoints require the header:

```
Authorization: Bearer <token>
```

### Auth

| Method | Endpoint          | Auth | Description          |
|--------|-------------------|------|----------------------|
| POST   | `/auth/register`  | No   | Register a new user  |
| POST   | `/auth/login`     | No   | Login, receive JWT   |
| POST   | `/auth/logout`    | Yes  | Revoke current token |

**Register** `POST /auth/register`
```json
{
  "email": "example@example.com",
  "password": "example",
  "fullName": "example"
}
```
Response: `201 Created`

**Login** `POST /auth/login`
```json
{
  "email": "example@example.com",
  "password": "example"
}
```
Response:
```json
{
  "token": "example...",
  "expiresAt": "2026-05-03T10:00:00"
}
```

**Logout** `POST /auth/logout`
Pass the JWT in `Authorization: Bearer <token>`. Response: `200 OK`

---

### Projects

| Method | Endpoint          | Auth | Role     | Description                         |
|--------|-------------------|------|----------|-------------------------------------|
| POST   | `/projects`       | Yes  | Any user | Create a project (caller = OWNER)   |
| GET    | `/projects`       | Yes  | Any user | List projects the caller belongs to |
| GET    | `/projects/{id}`  | Yes  | Member+  | Get a single project                |

**Create Project** `POST /projects`
```json
{ "name": "example" }
```
Response: `201 Created`
```json
{
  "id": 1,
  "name": "example",
  "ownerId": 1,
  "createdAt": "2026-05-02T09:00:00"
}
```

---

### Project Members

| Method | Endpoint                        | Auth | Role  | Description  |
|--------|---------------------------------|------|-------|--------------|
| POST   | `/projects/{projectId}/members` | Yes  | OWNER | Add a member |

**Add Member** `POST /projects/1/members`
```json
{
  "userId": 7,
  "role": "MEMBER"
}
```
Response: `201 Created`

---

### Tasks

| Method | Endpoint                                | Auth | Role          | Description               |
|--------|-----------------------------------------|------|---------------|---------------------------|
| POST   | `/projects/{projectId}/tasks`           | Yes  | Member+       | Create a task             |
| GET    | `/projects/{projectId}/tasks`           | Yes  | Member+       | List all tasks in project |
| PATCH  | `/projects/{projectId}/tasks/{taskId}`  | Yes  | Assignee only | Update a task             |
| DELETE | `/projects/{projectId}/tasks/{taskId}`  | Yes  | Member+       | Delete a task             |

**Create Task** `POST /projects/1/tasks`
```json
{
  "title": "example",
  "description": "example",
  "status": "TODO",
  "assigneeIds": [7, 12]
}
```

Response: `201 Created`
```json
{
  "id": 5,
  "projectId": 1,
  "title": "example",
  "description": "example",
  "status": "TODO",
  "assigneeIds": [1, 2],
  "createdAt": "2026-05-02T09:05:00",
  "updatedAt": "2026-05-02T09:05:00"
}
```

**Update Task** `PATCH /projects/1/tasks/5`
All fields are optional (partial update):
```json
{
  "status": "IN_PROGRESS",
  "assigneeIds": [7]
}
```

---

### Example curl Requests

```bash
# Register
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"example@example.com","password":"example","fullName":"example"}'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"example@example.com","password":"example"}'

# Create project
curl -X POST http://localhost:8080/projects \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"name":"example"}'

# Add member (must be project OWNER)
curl -X POST http://localhost:8080/projects/1/members \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"userId":7,"role":"MEMBER"}'

# Create task
curl -X POST http://localhost:8080/projects/1/tasks \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"title":"Fix bug","status":"TODO","assigneeIds":[7]}'

# Update task (must be an assignee)
curl -X PATCH http://localhost:8080/projects/1/tasks/5 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"status":"DONE"}'

# Logout
curl -X POST http://localhost:8080/auth/logout \
  -H "Authorization: Bearer <token>"
```

---

## Theory Questions

### 1. Database Design

**Why these tables?**

The schema maps directly to the domain: a `user` has many `projects` through `project_members`, and each `project` has many `tasks`. 
Relationships that are many-to-many (user↔project, user↔task) are resolved through explicit join tables (`project_members`, `task_assignees`) 
rather than JPA join tables so that extra columns (like `role`) can live on the join table itself. 
The `token_blacklist` table exists solely to support stateless logout — it stores revoked JWTs until they naturally expire, at which point they can be pruned. 
The `task_events` table is a log of what happened to a task (status changes, assignments), kept separate from the task itself to avoid polluting the primary row.

**Relationships:**

- `User` → `Project`: many-to-many via `project_members` (with a `role` column: `OWNER` or `MEMBER`)
- `Project` → `Task`: one-to-many (a task belongs to exactly one project)
- `Task` → `User`: many-to-many via `task_assignees`
- `User` → `TokenBlacklist`: one-to-many (a user can have multiple revoked tokens in flight)

---

### 2. API Design

**Why these endpoints and HTTP methods?**

The API follows REST conventions: resources are nouns, HTTP verbs express intent. 
`POST` creates, `GET` reads, `PATCH` partially updates (rather than `PUT` because callers only need to send the fields they want to change), and `DELETE` removes. 
Tasks are nested under `/projects/{projectId}/tasks` because a task does not exist outside a project. 
Separate endpoints for members (`/projects/{projectId}/members`) keep project management concerns out of the project resource itself, which only returns top-level metadata. 
`GET /projects` returns only the projects the caller belongs to rather than all projects in the database, so the list is immediately useful and does not require client-side filtering.

---

### 3. Security

**How are routes protected?**

All routes except `/auth/**` require a valid JWT. The `JwtFilter` (a `OncePerRequestFilter`) intercepts every request, extracts the `Authorization: Bearer` header, validates the signature and expiry with `JwtUtil`, and rejects the request silently if the token is invalid or if the token's exact string is present in the `token_blacklist` table. When the token passes, the filter loads the user from the database and sets the `Authentication` in Spring's `SecurityContextHolder`.

**How is cross-user data access prevented?**

Method-level authorization is enforced via `@PreAuthorize` with custom SpEL expressions that delegate to `PreauthService`:

- `@authService.isMember(#projectId)` — checks that the authenticated user has a row in `project_members` for the given project before allowing read or write access.
- `@authService.isOwner(#projectId)` — additionally checks the `role = OWNER` column before allowing member management.
- `@authService.isAssignTask(#taskId)` — checks that the user is in `task_assignees` for the given task before allowing an update. A project member cannot update a task they were not assigned to.

The current user's identity is always resolved from the JWT via `SecurityContextHolder`, so a caller cannot impersonate another user by supplying a different user ID.

---

### 4. Error Handling

A `@RestControllerAdvice` (`GlobalExceptionHandler`) centralizes error responses. Every error response follows the same shape:

```json
{
  "message": "...",
  "timestamp": "2026-05-02T10:00:00"
}
```

| Exception                   | HTTP Status | When it fires                                           |
|-----------------------------|-------------|---------------------------------------------------------|
| `ResourceNotFoundException` | 404         | Entity looked up by ID does not exist                   |
| `IllegalArgumentException`  | 400         | Invalid input (duplicate email, duplicate member, etc.) |
| `AccessDeniedException`     | 403         | `@PreAuthorize` check fails                             |
| Any other `Exception`       | 500         | Unexpected server error; full stack trace is logged internally but not exposed to the caller |

---

### 5. Background Jobs

**Current state**

The `task_events` table is in place to log events against a task (event type, JSON payload, timestamp). 
The `spring-boot-starter-kafka` dependency is already included in `pom.xml` as the intended transport layer, but a full Kafka producer/consumer is not yet wired up — this was cut due to time constraints and is the next planned addition.

**If time permits — planned Kafka integration**

When a task's status changes to `DONE`, a `TaskService` would publish a message to a `task.completed` Kafka topic. 
A separate `@KafkaListener` consumer would pick it up asynchronously, write the event to `task_events`, and trigger any downstream side effects (e.g. sending a notification, updating project statistics).

**What happens if the background job fails?**

Kafka retains the message on the topic if a consumer fails to process it. 
The consumer would be configured with a dead-letter topic (DLT) — after a configurable number of retries.

**How would duplicate events be avoided?**

Use idempotent consumers. 
Before processing an event, the consumer checks whether a `TaskEvent` row with the same `task_id` + `eventType` already exists. 
If it does, the message is acknowledged and skipped. 

