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