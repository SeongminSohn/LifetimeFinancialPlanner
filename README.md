# Lifetime Financial Planner

A full-stack web application for modeling lifetime financial scenarios and running Monte Carlo simulations to forecast financial outcomes over a user's lifetime.

## What It Does

The Lifetime Financial Planner allows users to:

- Create and manage multiple financial scenarios with different assumptions
- Model income, investment, and expense events over time
- Run Monte Carlo simulations using statistical distributions (normal, uniform, triangular) to account for uncertainty
- Calculate federal and state tax implications based on 2025 tax brackets
- Define expense withdrawal strategies for retirement planning
- Import and export scenarios as YAML files
- Visualize simulation results through interactive stacked bar charts

## Tech Stack

### Backend
- **Language:** Java 11
- **Framework:** Spring Boot 2.7
- **ORM / Persistence:** Spring Data JPA, MyBatis, HikariCP connection pool
- **Database:** Oracle DB (configured via `application.yml`)
- **Build Tool:** Gradle
- **API Documentation:** SpringDoc OpenAPI (Swagger UI)
- **Other Libraries:** Lombok, Gson, Apache Commons Lang3

### Frontend
- **Framework:** React 19
- **Routing:** React Router 7
- **Build Tool:** Vite
- **HTTP Client:** Axios
- **Charting:** Recharts
- **Testing:** Jest, React Testing Library

### Testing & Quality
- **Backend:** JUnit 5, JaCoCo (code coverage)
- **Frontend:** Jest, React Testing Library

## Project Structure

```
├── src/main/java/com/app/lifetimefinancialplanner/
│   ├── config/          # CORS, async, MyBatis, tax data loader
│   ├── controller/      # REST API controllers (user, scenario, simulation, charts, events)
│   ├── domain/
│   │   ├── entity/      # JPA entities (User, Scenario, Simulation, Investment, Events, etc.)
│   │   ├── dto/         # Data transfer objects
│   │   └── embeddable/  # Reusable compound value types
│   ├── repository/      # Spring Data JPA repositories
│   └── service/         # Business logic (simulation engine, tax, sampling, distributions)
├── src/main/frontend/   # React SPA (Vite)
│   └── src/
│       ├── pages/       # All page components
│       └── component/   # Reusable UI components (charts, etc.)
└── src/main/resources/
    ├── application-template.yml
    └── static/          # 2025 federal and state tax bracket YAML files
```

## Getting Started

### Prerequisites

- Java 11+
- Node.js 18+
- Oracle Database (configure connection in `application.yml` based on `application-template.yml`)

### Run the Full Application

The Gradle build automatically installs frontend dependencies, builds the React app, and bundles everything into a single JAR:

```bash
./gradlew clean build
java -jar build/libs/LifetimeFinancialPlanner-1.0.0.jar
```

Or run directly with Gradle:

```bash
./gradlew bootRun
```

The app will be available at `http://localhost:8080`.

### Frontend Development Server

To run the React frontend separately with hot reload:

```bash
cd src/main/frontend
npm install
npm run dev
```

The dev server proxies API requests to the Spring Boot backend.

## Running Tests

### Backend

```bash
# Run tests
./gradlew test

# Generate code coverage report (output: build/reports/jacoco/test/html/index.html)
./gradlew jacocoTestReport
```

### Frontend

```bash
cd src/main/frontend

# Run tests
npm test

# Run tests with coverage
npm run test:coverage
```

## API Documentation

Once the application is running, Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

Key API groups:
- `/api/users` — registration and login
- `/api/scenarios` — scenario CRUD
- `/api/simulations` — run and retrieve simulations
- `/api/income-events`, `/api/expense-events`, `/api/invest-events` — event management
- `/api/investments`, `/api/investment-types` — investment management
- `/api/charts` — chart data for simulation results

## Deployment

Deployment was not in scope for this project. If deployed, the recommended approach would be to containerize the application using Docker — packaging the Spring Boot JAR (with the React frontend bundled inside) into a Docker image and running it as a container alongside an Oracle DB instance via Docker Compose.
