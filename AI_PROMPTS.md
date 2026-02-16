# AI Generation Prompt

Below is the specific prompt used to generate this project:

---

**Role**: Act as a Senior Java Spring Boot Engineer.
**Task**: Build a complete "JetRental" Web Application using the specific architecture and tech stack defined below.
**Constraint**: Do not omit the CriteriaBuilder search logic or the dual (Web vs. API) controller structure.

### 1. Tech Stack & Configuration
-   **Framework**: Spring Boot 4.0.2
-   **Language**: Java 17
-   **Build Tool**: Maven
-   **Database**: H2 In-Memory (embedded)
-   **Template Engine**: Thymeleaf
-   **Boilerplate**: Lombok (@Data, @Builder, @RequiredArgsConstructor)
-   **Dependencies**: spring-boot-starter-data-jpa, spring-boot-starter-web, spring-boot-starter-thymeleaf, spring-boot-starter-test, mockito-core.

### 2. Core Entities (Relational Logic)
Create the following JPA Entities in the entity package:

**Jet**:
-   Fields: id (Integer), brand (String), model (String), tailNumber (String, unique), seats (int), pricePerDay (double), available (boolean).

**Customer**:
-   Fields: id (Integer), firstName, lastName, email (unique), pilotLicense, countryCode.

**RentalEvent**:
-   Fields: id (Integer), rentalDate (Date), returnDate (Date), totalCost (double), isClosed (boolean).
-   Relationships:
    -   @ManyToOne mapping to Jet (jetRented).
    -   @ManyToOne mapping to Customer (customerRenting).

### 3. Repository Layer
-   Standard JpaRepository for all entities.
-   **Advanced Search Requirement**: Implement a custom repository (JetRepositoryCustom and JetRepositoryImpl) using JPA CriteriaBuilder to filter Jets by:
    -   Min/Max seats.
    -   Max price.
    -   Brand and Model (exact match).

### 4. Service Layer (Business Logic)
-   **JetService**: Handle CRUD. addOrUpdateJet must be @Transactional.
-   **CustomerService**: Handle CRUD.
-   **RentalEventService**:
    -   `rentJet(RentalEvent event)`:
        -   Validate Jet availability.
        -   Set Jet available = false.
        -   Calculate initial cost if dates are provided.
        -   Save Event and Jet.
    -   `returnJet(int eventId)`:
        -   Validate event is not already closed.
        -   Set returnDate to NOW if null.
        -   Recalculate totalCost based on actual duration (minimum 1 day).
        -   Set Jet available = true.
        -   Set Event closed = true.

### 5. Controller Architecture (Dual Layer)
-   **API Layer (/api/...)**: RestControllers for Jet, Customer, and RentalEvent returning JSON.
-   **Web Layer (Root /)**: Controllers returning Thymeleaf views strings (e.g., "jet/list").
-   **Configuration**: Include initBinder in RentalEventWebController to handle Date formatting (yyyy-MM-dd).

### 6. Thymeleaf Views (Templates)
Create the following HTML structure in src/main/resources/templates:
-   **jet/**: list.html, add.html, edit.html, details.html, filter.html, filteredJets.html, error.html.
-   **customer/**: list.html, add.html, edit.html, details.html, error.html.
-   **rental/**: list.html (Show "Return Jet" button only if active), add.html (Dropdowns for Jets/Customers), details.html, edit.html, error.html.
-   **home.html**: Simple navigation landing page.

### 7. Data Initialization
Create a data.sql file to seed:
-   3 Jets (e.g., Gulfstream G650, Bombardier Global 7500, Cessna Citation X).
-   2 Customers (e.g., Maverick, Iceman).

### 8. Testing
-   Include JUnit 5 / Mockito unit tests.
-   The Tests should have a coverage of at least 80% of the whole project.
