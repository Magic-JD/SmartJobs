# SmartJobs

## SmartJobs: Intelligent Career Matching and Analysis

SmartJobs is a sophisticated web application designed to revolutionize the job search and recruitment process. It leverages advanced AI to provide intelligent matching between candidates and roles, deep CV analysis, and comprehensive insights into career trajectories and criteria.

---

## Features

* **AI-Powered Role Matching:** Automatically match candidate profiles against job roles based on predefined criteria.
* **Detailed CV Analysis:** Analyze CVs to extract relevant skills, experience, and qualifications.
* **Customizable Criteria:** Define and manage specific criteria for roles and candidates.
* **Credit System:** A built-in credit system for managing usage of premium features (e.g., AI analyses).
* **User Management:** Secure user authentication and authorization.
* **Dynamic UI:** Built with **HTMX** and **Thymeleaf** for a responsive user experience.

---

## Technologies Used

* **Spring Boot 3.5.0:** Core application framework.
* **Java 21:** Programming language.
* **PostgreSQL:** Primary database.
* **Testcontainers 1.20.2:** For providing isolated, real database environments for integration testing.
* **Spring Data JPA / Hibernate:** For database interaction and ORM.
* **Spring Security 6.4.2:** For authentication and authorization.
* **Maven:** Build automation tool.
* **Lombok:** To reduce boilerplate code.
* **Resilience4j 2.2.0:** For fault tolerance and resilience patterns.
* **OpenAPI (Springdoc) 2.8.4:** For API documentation.
* **Apache PDFBox 3.0.2 & Apache POI 5.2.5:** For document processing.
* **HTMX 1.5.0 & Hyperscript 0.8.1:** For dynamic frontend interactions.
* **Thymeleaf & j2html 1.5.0:** For server-side templating.
* **Bucket4j 8.10.1 & Caffeine 3.0.4:** For rate limiting and caching.
* **Vavr 0.10.4:** Functional programming library for Java.

---

## Getting Started

These instructions will help you set up and run a trial version of SmartJobs on your local machine using a self-contained database.

### Prerequisites

Before you begin, ensure you have the following installed:

* **Java Development Kit (JDK) 21**: Make sure `JAVA_HOME` is set correctly.
* **Maven 3.8.x** (or newer):
* **Docker Desktop (or Docker Engine)**: Required for Testcontainers to run the PostgreSQL database. **Ensure Docker is running** before proceeding.

### Installation

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/your-username/SmartJobs.git](https://github.com/your-username/SmartJobs.git)
   cd smart-jobs
   ```
2. **Build the project:**
   ```bash
   mvn clean install
   ```
   This command compiles the code, runs unit tests, and packages the application.

---

## Trialing the Application (Testcontainers Environment)

To experience SmartJobs, you can run a local instance that uses a PostgreSQL database spun up automatically by Testcontainers. This setup ensures a clean and isolated environment for your trial, without connecting to any external backend services.

To start the application in trial mode:

```bash
mvn spring-boot:test-run \
  -Dspring-boot.run.main-class=org.smartjobs.TestSmartJobs \
  -Dspring.profiles.active=test
```

This command will:

* Start a new **PostgreSQL container** in Docker.
* Initialize the database schema (`schema.sql`) and populate it with sample data (`data.sql`).
* Launch the SmartJobs application, connecting it to this temporary PostgreSQL database.

The application will be available on a random, available **HTTPS port** (e.g., `https://localhost:54321`). Please **check your console output** for the exact assigned port.

You can log in with the following trial credentials:

* **Username:** `email@email.com`
* **Password:** `password1`

---

## Running Tests

SmartJobs includes a comprehensive suite of unit and integration tests.

### Running All Tests

To run all tests (unit and integration), execute:

```bash
mvn test
```

### Running Integration Tests Specifically

Integration tests leverage Testcontainers to provide a fresh, isolated PostgreSQL database for each test run. They use the `test` profile to ensure consistent environments and initialize data **before** each test method for reliable execution.