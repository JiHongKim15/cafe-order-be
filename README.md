# Cafe Order API Backend

## Introduction

This is the backend API for a cafe ordering system. It's built with Spring Boot and provides RESTful APIs for managing orders, products, and members.

## Getting Started

### Prerequisites

- Java 17
- Gradle

### Building the project

To build the project, run the following command in your terminal:

```bash
./gradlew build
```

### Running the application

Once the project is built, you can run the application using the following command:

```bash
java -jar build/libs/order-0.0.1-SNAPSHOT.jar
```

The application will be available at `http://localhost:8080`.

## Running the tests

To run the tests, use the following command:

```bash
./gradlew test
```

## API Documentation

The API documentation is available through Swagger UI once the application is running. You can access it at:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Technologies Used

- **Framework:** Spring Boot 3.5.6
- **Language:** Java 17
- **Build Tool:** Gradle
- **Database:** H2 (In-memory)
- **API Documentation:** Springdoc OpenAPI (Swagger UI)
- **Libraries:**
    - Spring Data JPA
    - Spring Web
    - Spring AOP
    - Resilience4j
    - Lombok
