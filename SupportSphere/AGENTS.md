# SupportSphere Project Context and Rules

## Detailed Application Context
**SupportSphere** is a client support and ticket-management platform.
- **Frontend**: Angular 16+, HTML, CSS (Running on port 8081). Frontend is strictly OUT OF SCOPE.
- **Backend**: Java 17, Spring Boot 3.0.1, MySQL / H2, JWT (Running on port 8080).
- **Core Purpose**: Allows clients to create and manage support tickets, support agents to handle them, managers to assign agents, and users to provide feedback.
- **AI Chatbot (Phase 2)**: Integrates Google Gemini API for semantic FAQ search.

**Main Domain Entities:**
1. **User**: Represents application users/clients. Roles: `Manager`, `Client`. Password stored as BCrypt hash.
2. **Support Agent**: Personnel handling tickets. Status: `Available`, `Unavailable`.
3. **Ticket**: Core entity. Lifecycle: `OPEN` -> `IN_PROGRESS` -> `RESOLVED` -> `CLOSED`. Includes priority (High/Medium/Low) and issue category.
4. **Feedback**: Users can provide feedback on resolved/closed tickets.
5. **Error Log**: Used for application-level error history (separate from standard SLF4J logs).

## Strict Refactoring Rules
1. **Architecture**: Do NOT rewrite from scratch. Preserve existing `Controller -> Service -> Repository -> Database` layers.
2. **DTOs**: Do NOT restructure or modify existing DTOs. Do not introduce new Request/Response classes.
3. **Frontend**: Do NOT touch the Angular frontend code.
4. **APIs**: Preserve existing endpoint URLs, HTTP methods, and JSON request/response formats.
5. **JPA/Entities**: Do NOT use `Optional` in JPA entity fields. Legitimate nulls remain nullable.
6. **Security**: Maintain stateless JWT authentication and role/ownership-based authorization.
7. **Logging**: Use SLF4J. NEVER log passwords, password hashes, JWTs, or Authorization headers.

## Phase Progress Tracker
The current refactoring is broken down into 6 phases.

- [ ] **Phase 1**: Implement SLF4J logging, proper configuration in `application.properties`, and Request/Correlation ID via MDC.
- [ ] **Phase 2**: Refactor `Optional` usage (`.orElseThrow()`), repository return types, and introduce `existsBy...` queries.
- [x] **Phase 3**: Clean up the service layer (move business logic from controllers to services), ensure proper transaction boundaries (`@Transactional`).
- [x] **Phase 4**: Convert fixed String domain values to Java `enums` and persist using `@Enumerated(EnumType.STRING)`.
- [ ] **Phase 5**: Review Security/JWT logging and ensure robust authorization/ownership handling.

## Changelog
* **Phase 4 (Completed)**: Created `TicketPriority`, `SupportAgentStatus`, and `UserRole` enums with Jackson `@JsonValue` mapping. Updated `Ticket`, `SupportAgent`, and `User` entities to use `@Enumerated(EnumType.STRING)` and updated `UserPrinciple`.
* **Phase 3 (Completed)**: Added `@Transactional` annotations to database-mutating operations in `TicketServiceImpl`, `SupportAgentServiceImpl`, `FeedbackServiceImpl`, and `UserServiceImpl`. Verified `LOGGER.info` usage on key service methods.
