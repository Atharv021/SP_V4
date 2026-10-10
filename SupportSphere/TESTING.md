# SupportSphere Backend Testing Guide

## Prerequisites
- Java 17 must be installed and available in your `PATH` (`java -version`).
- Maven must be installed and available in your `PATH` (`mvn -version`).
- The application uses an in-memory H2 database for testing, so no external database server is required.

## Test Environment
- **Isolated Testing**: The test suite uses the `src/test/resources/application.properties` configuration which provisions an isolated H2 in-memory database in MySQL mode.
- **Mocked Integrations**: The Gemini API calls and authentication tokens are either mocked or use test-specific properties to ensure tests do not rely on production environments or active API keys.

## Running Tests
A unified shell script is provided to run all tests and handle basic environment validation.

### Linux/macOS
From the repository root (`SupportSphere/`), run:
```bash
./run-tests.sh
```

### Windows (Git Bash or WSL)
From the repository root (`SupportSphere/`), run:
```bash
bash ./run-tests.sh
```

### Directly with Maven
You can also navigate to the backend project and run Maven directly:
```bash
cd springapp
mvn clean test
```

## Running Specific Tests
To run a single test class or a specific method for debugging, use Maven's `-Dtest` argument from within the `springapp` directory.

Run a single class:
```bash
mvn test -Dtest=TicketServiceImplTest
```

Run a specific method:
```bash
mvn test -Dtest=TicketServiceImplTest#testAddTicket_Success
```

## Types of Tests
- **Unit Tests**: Test individual service logic using Mockito to isolate dependencies (e.g., `TicketServiceImplTest`).
- **Repository Tests**: Verify JPA queries and persistence constraints using `@DataJpaTest` with an in-memory DB.
- **Controller Tests**: Verify the web layer, HTTP status codes, and JSON mappings using `@WebMvcTest` with mocked services.
- **Integration Tests**: Verify the full Spring context, including security, filters, persistence, and HTTP endpoints using `@SpringBootTest` (e.g., `SecurityIntegrationTest`). E2E workflows are tested here.

## Viewing Test Reports
After running the tests, Maven Surefire generates detailed reports.
- **Location**: `springapp/target/surefire-reports/`
- **Format**: Both plain text (`.txt`) and XML (`.xml`) reports are available for each test class.
- **Failures**: If tests fail, the shell script will exit with an error and output the failure summary. Check the specific `.txt` file in the report folder for stack traces.

## Known Limitations and Coverage
- E2E workflows heavily cover the `Ticket`, `SupportAgent`, and `Feedback` lifecycles based on SRS requirements.
- Edge cases in Chatbot lexical matching are covered, but deep semantic embedding evaluation uses mocked responses.
- Some edge cases involving complex database locking or concurrency have been mocked at the service layer rather than fully integrated to keep tests deterministic.
