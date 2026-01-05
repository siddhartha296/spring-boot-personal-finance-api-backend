# spring-boot-personal-finance-api-backend

This README file provides an overview and setup guide for the **Spring Boot Personal Finance API Backend**, a RESTful service designed to manage personal finances including users, categories, expenses, and budgets.

---

# Personal Finance API Backend

A Spring Boot-based REST API designed to help users track expenses, manage budget limits, and organize spending by categories. This project uses MySQL as its primary database and follows a standard Spring Data JPA architecture.

## Features

* **User Management**: Register and manage user profiles.
* **Expense Tracking**: Create, update, delete, and view expenses with details like payment methods (CASH, CARD, UPI, etc.) and receipt URLs.
* **Budget Management**: Set budget limits for specific categories within defined date ranges.
* **Real-time Status**: Calculate budget vs. actual spending, including percentage used and "over budget" alerts.
* **Category Organization**: Categorize transactions with custom names, icons, and colors.
* **CORS Enabled**: Pre-configured to allow requests from cross-origin frontend applications.

## Tech Stack

* **Framework**: Spring Boot 4.0.1.
* **Language**: Java 17.
* **Database**: MySQL.
* **ORM**: Spring Data JPA.
* **Build Tool**: Maven.

## API Endpoints

### Users (`/api/users`)

* `POST /api/users`: Create a new user.
* `GET /api/users`: Retrieve all users.
* `GET /api/users/{id}`: Get user by ID.
* `GET /api/users/email/{email}`: Get user by email.

### Expenses (`/api/expenses`)

* `POST /api/expenses`: Log a new expense.
* `GET /api/expenses/user/{userId}`: Get all expenses for a specific user.
* `GET /api/expenses/user/{userId}/total`: Get total expenditure for a user.
* `GET /api/expenses/user/{userId}/date-range`: Filter expenses by date.

### Budgets (`/api/budgets`)

* `POST /api/budgets`: Set a category budget.
* `GET /api/budgets/{id}/status`: Check spending status relative to the budget limit (includes `isOverBudget` and `alertThresholdReached` flags).
* `GET /api/budgets/user/{userId}/active`: View currently active budgets for a user.

### Categories (`/api/categories`)

* `GET /api/categories`: List all available expense categories.
* `POST /api/categories`: Create a new category (e.g., Food, Travel).

## Getting Started

### Prerequisites

* JDK 17 or higher.
* MySQL Server.

### Configuration

1. Open `src/main/resources/application.properties`.
2. Configure your MySQL credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
spring.datasource.username=your_username
spring.datasource.password=your_password

```



### Running the Application

Use the included Maven wrapper to start the server:

```bash
# Windows
./mvnw.cmd spring-boot:run

# Linux/macOS
./mvnw spring-boot:run

```

The API will be available at `http://localhost:8080`.

## License

This project is licensed under the **MIT License**.
