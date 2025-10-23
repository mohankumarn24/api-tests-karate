package net.projectsync.karatedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KarateDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(KarateDemoApplication.class, args);
	}
}

/**
 * We have added unit tests and api tests for this project to achieve more code coverage
 * - JUnit test cases (net.projectsync.karatedemo)
 * - Karate API tests (karate.bankproducts)
 *
 * Unit Test Report:
 * - file:///D:/dev/github/api-tests-karate/htmlReport/index.html
 *
 * Karate Tests Report:
 * - file:///D:/dev/github/api-tests-karate/build/karate-reports/karate-summary.html
 */

/*
Unit Test vs API Test vs Integration Test

1. Unit Test
------------
Definition:
- Tests the smallest unit of code in isolation, usually a single method or class, to ensure it works as expected.
- Does NOT involve external dependencies like databases, APIs, or messaging systems (these are typically mocked).

Focus:
- Logic correctness of a single unit of code.
- Inputs → Outputs for functions/methods.
- Covers positive, negative, and edge cases.

Tools:
- JUnit, TestNG, Mockito, Spock, AssertJ.

Example:
- Test a method that calculates interest for a bank product.
- Test a service method that formats account numbers.

Characteristics:
Aspect        | Unit Test
--------------|-------------------------
Scope         | Single class or method
Dependencies  | Mocked or none
Speed         | Very fast
Purpose       | Ensure code logic is correct in isolation

2. API Test (Functional/API-level test)
---------------------------------------
Definition:
- Tests the behavior of a single API endpoint (or a small set of endpoints) to ensure it works as expected.

Focus:
- Input → Output of the API.
- Validates request/response, status codes, headers, body, error handling.
- Usually does not care about internal implementation.
- Can be done without starting the full application, using mocks or stubs.

Tools:
- Karate, Postman, RestAssured, SoapUI, HTTP client libs.

Example:
- Test POST /bankproducts creates a product and returns 201.
- Test GET /bankproducts/{id} returns the correct product.

Characteristics:
Aspect        | API Test
--------------|-------------------------
Scope         | Single endpoint or feature
Dependencies  | Usually minimal, may use mocks
Speed         | Fast
Purpose       | Ensure API behaves correctly for given inputs

3. Integration Test
-------------------
Definition:
- Tests that multiple parts of the application work together correctly. Can include API endpoints, database, messaging systems, external services, etc.

Focus:
- Ensures that components integrate properly.
- May check end-to-end flows, including persistence, transactions, messaging, etc.
- Typically runs inside the application context (e.g., Spring Boot Test).

Tools:
- Spring Boot @SpringBootTest, JUnit, Testcontainers, WireMock.

Example:
- Test creating a bank product via API and verify it is saved in the database.
- Test sending a message to Kafka and ensure another service processes it.

Characteristics:
Aspect        | Integration Test
--------------|-------------------------
Scope         | Multiple components
Dependencies  | Real DB, external services, full app context
Speed         | Slower than API/unit tests
Purpose       | Ensure end-to-end integration works

Key Differences
---------------
Feature        | Unit Test                   | API Test                          | Integration Test
---------------|----------------------------|------------------------------------|--------------------------------------
Level          | Code logic                 | Functional                         | Component / End-to-end
Scope          | Single method/class        | Single API                         | Multiple components together
External deps  | Mocked or none             | Can be mocked                      | Usually real or test containers
Speed          | Fastest                    | Fast                               | Slower
Failure cause  | Logic error in code        | Logic/contract of API              | Integration issues between components

Rule of thumb:
- Unit tests            ->  test the code logic in isolation
- API tests             ->  test the API endpoints
- Integration tests     ->  test if multiple components work together correctly
 */