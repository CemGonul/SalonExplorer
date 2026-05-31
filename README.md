# Salon Explorer

Salon Explorer is a full-stack application for browsing beauty, nail and spa salons in selected Warsaw districts. The current dataset focuses on Mokotow, Srodmiescie and Wola, collected with the Google Places API and imported into PostgreSQL.

## Features

- Browse the salon list
- Filter salons by district
- Sort salons by name, rating or review count
- Open a detail page for a single salon
- Edit selected salon fields: name, address and website
- Prevent exact duplicate salon records by name and address
- Seed PostgreSQL from a cleaned JSON dataset

## Tech Stack

- Backend: Java 17, Spring Boot, Spring Data JPA, Hibernate
- Database: PostgreSQL
- Migrations: Flyway
- Frontend: Angular, Angular Router, Angular HttpClient, SCSS
- Data collection: Java, Google Places API
- Dev setup: Docker Compose
- Build tools: Maven, npm

## Technical Solution

The project is split into three parts: backend, frontend and data-fetch.

The `data-fetch` module is a small Java application that collects salon data from Google Places API. The result is saved as a cleaned JSON file so the data can be checked before importing it into the database.

The `backend` module is a Spring Boot REST API. It uses PostgreSQL as the main database, Flyway for schema migration and Spring Data JPA/Hibernate for database access. The backend exposes endpoints for listing salons, loading available districts, viewing a single salon and updating selected salon fields.

The `frontend` module is an Angular application. It uses Angular Router for navigation and Angular HttpClient through a dedicated `SalonApiService` to communicate with the backend. The frontend only handles the UI; filtering, sorting and updates are handled by the backend.

Docker Compose is used to run PostgreSQL locally with the same setup each time.

## Project Structure

```text
SalonExplorer/
  backend/      Spring Boot REST API
  frontend/     Angular application
  data-fetch/   Java data collection tool
  docker-compose.yml
```

## Prerequisites

- Java 17 or newer
- Node.js and npm
- Docker Desktop

## How to Run

Start PostgreSQL from the project root:

```powershell
docker compose up -d
```

Start the backend:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

If the database is empty, import the prepared JSON data once:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--app.import-data.enabled=true"
```

After the import finishes, stop the backend with `Ctrl + C` and start it normally again:

```powershell
.\mvnw.cmd spring-boot:run
```

Start the frontend in a second terminal:

```powershell
cd frontend
npm install
npm.cmd start
```

Open the application:

```text
http://localhost:4200
```

## API Endpoints

```text
GET  /api/salons
GET  /api/salons/districts
GET  /api/salons/{id}
PUT  /api/salons/{id}
```

The list endpoint supports optional query parameters:

```text
district
sortBy=name|rating|reviewCount
direction=asc|desc
```

## Data Collection

The data was collected with the separate `data-fetch` Java module. It queried Google Places for three Warsaw districts: Mokotow, Srodmiescie and Wola. For each district, it searched beauty salons, spa salons and nail salons.

The cleaned result is stored in:

```text
backend/src/main/resources/data/salons_cleaned.json
```

The application does not require a Google Places API key to run. The key was only needed during the data collection step.

## Tests

Run backend tests:

```powershell
cd backend
.\mvnw.cmd test
```

Build the frontend:

```powershell
cd frontend
npm.cmd run build
```

## What I Would Improve With More Time

- Improve service and price data with a different collection approach. Google Places returned reliable names, addresses, ratings and websites, but service details are category-level and price range was not available in the collected dataset.
- Expand the dataset to more Warsaw districts and later to other Polish cities.
- Add text search by salon name, address or service category.
- Add more filters, such as service category, minimum rating and review count.
- Add a map view to make location-based browsing easier.
- Add pagination after expanding the dataset with more salons.
