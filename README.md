# URL Shortener (Backend Only)

> A backend infrastructure for shortening URLs, inspired by Bitly and similar services. Built with Java & Spring Boot, using Base62 encoding to generate compact, shareable short links.

---

## About

This is a **backend-only** URL shortening service. Users can register, authenticate, and submit long URLs to receive shortened equivalents. When a shortened URL is accessed, the service resolves it back to the original URL and redirects the user. The service also tracks how many times each short link has been visited using an asynchronous hit-count mechanism.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.0.3 |
| Database | PostgreSQL |
| Cache | Redis |
| Message Queue | Apache Kafka |
| Authentication | JWT (JSON Web Tokens) |
| Build Tool | Gradle |
| Boilerplate Reduction | Lombok |

---

## Features

- Shorten long URLs using **Base62 encoding**
- **User registration and login** with JWT-based stateless authentication
- **Redis caching** for fast short URL resolution without repeated DB hits
- **Kafka-based async hit-count tracking** — redirect performance is not blocked by analytics writes
- **PostgreSQL** for persistent storage of users and URLs
- Clean layered architecture (Controller → Service → Repository)

---

## Project Structure

```
src/main/java/com/seetharam/urlshortener/
├── config/          # Spring Security, Redis, and Kafka configuration
├── controller/      # REST API endpoint definitions
├── dto/             # Data Transfer Objects for request/response
├── entity/          # JPA database models
├── exceptions/      # Custom exception handling
├── kafka/           # Kafka producer/consumer for async analytics
├── repository/      # Spring Data JPA repository interfaces
├── service/         # Business logic (UrlService, UserService)
└── UrlShortenerApplication.java
```

---

## Architecture Overview

```
Client Request
     │
     ▼
REST Controller
     │
     ▼
Service Layer
   ├── Check Redis cache for short URL
   ├── If miss → query PostgreSQL
   ├── Publish hit-count event to Kafka (async)
   └── Return original URL → redirect
     │
     ▼
Kafka Consumer → Update hit count in PostgreSQL
```

---

## Getting Started

### Prerequisites

- Java 17+
- Gradle
- Docker & Docker Compose (for Redis and Kafka)
- PostgreSQL (running locally or via Docker)

### 1. Start Infrastructure Services

```bash
docker-compose up -d
```

This starts:
- **Redis** on port `6379`
- **Apache Kafka** on port `9092`

### 2. Configure the Application

Update `src/main/resources/application.properties` (or `application.yml`) with your PostgreSQL credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/urlshortener
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Build and Run

```bash
./gradlew bootRun
```

---

## API Endpoints

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user | No |
| POST | `/api/auth/login` | Login and receive JWT token | No |
| POST | `/api/urls/shorten` | Shorten a URL | Yes |
| GET | `/{shortCode}` | Redirect to original URL | No |

---

## Key Design Decisions

- **Base62 encoding** generates short, URL-safe alphanumeric codes
- **Redis** caches resolved URLs to minimize database load on every redirect
- **Kafka** decouples hit-count tracking from the redirect flow, keeping latency low
- **JWT authentication** ensures only registered users can create shortened URLs

---

## Dependencies

- `spring-boot-starter-security` — Authentication & authorization
- `spring-boot-starter-data-jpa` — ORM with PostgreSQL
- `spring-boot-starter-webmvc` — REST API
- `spring-boot-starter-data-redis` — Redis caching
- `spring-kafka` — Kafka integration
- `jjwt-api / jjwt-impl / jjwt-jackson` — JWT token handling
- `lombok` — Reduces boilerplate code
- `postgresql` — JDBC driver


