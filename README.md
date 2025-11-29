# Analytics Service

A simple real-time analytics demo application built with Java 11 and Spring Boot.  
It ingests user events, processes them in real time, and exposes metrics over REST APIs.

---

## Features

- Real-time ingestion and processing of user events
- In-memory storage of analytics metrics
- Active user count for a configurable time window
- Top visited pages over a recent time window
- Active sessions per user
- Simple mock event generator to quickly populate data

---

## High-Level Architecture

- **Controller Layer**
    - Exposes REST endpoints under `/dashboard`.
    - Delegates to a dashboard service to fetch metrics.

- **Service Layer**
    - `DashboardService` and its implementation aggregate and return metrics.
    - `EventIngestionService` accepts raw events and passes them to the processor.
    - `RealTimeEventProcessor` updates metrics in the `MetricsStore`.

- **Metrics Store**
    - `MetricsStore` interface abstracts metric read/write operations.
    - `InMemoryMetricsStore` (implementation) keeps counters and windows in memory.

- **Models**
    - `ActiveUserMetric` – holds the active user count.
    - `PageViewMetric` – represents page URL and view count.
    - `UserSessionMetric` – represents active sessions for a given user.

- **Mock Event Generator**
    - `MockEventGenerator` periodically creates random user events.
    - Useful for demos and local testing without external traffic.

---

## REST Endpoints

Base path: `/dashboard`

1. `GET /dashboard`
    - Triggers mock events ingestion for a short duration.
    - Returns a simple message (e.g., `"Events Loaded"`).

2. `GET /dashboard/active-users`
    - Returns `ActiveUserMetric`.
    - Represents number of active users in the last 5 minutes.

3. `GET /dashboard/top-pages`
    - Returns a list of `PageViewMetric`.
    - Top N pages (e.g., 5) in the last 15 minutes.

4. `GET /dashboard/users/{userId}/active-sessions`
    - Returns `UserSessionMetric` for a specific `userId`.
    - Shows how many active sessions that user has in the last 5 minutes.

---

## How It Works (Flow)

1. The mock generator produces `UserEvent` objects (user, page, session, timestamp).
2. `EventIngestionService` receives each event and forwards it to `RealTimeEventProcessor`.
3. `RealTimeEventProcessor` updates:
    - Page views (`incrementPageView`)
    - Active users (`updateActiveUser`)
    - Sessions (`updateSession`)
4. `DashboardService` reads from `MetricsStore` using time windows and shapes data into:
    - `ActiveUserMetric`
    - `PageViewMetric`
    - `UserSessionMetric`
5. The controller returns these objects as JSON for each REST endpoint.

---

## Running the Application

1. Ensure you have:
    - Java 11+
    - Maven or Gradle (depending on build setup)

2. Build and run:
   ```bash
   # Example with Maven
   mvn clean install
   mvn spring-boot:run
   ```

3. Access endpoints:
    - Trigger mock events: `GET http://localhost:8080/dashboard`
    - Active users: `GET http://localhost:8080/dashboard/active-users`
    - Top pages: `GET http://localhost:8080/dashboard/top-pages`
    - Active sessions for a user:  
      `GET http://localhost:8080/dashboard/users/{userId}/active-sessions`

---

## Configuration & Extensibility

- The time windows (e.g., 5 minutes for active users, 15 minutes for top pages) are currently set in code but can be externalized to configuration.
- The `MetricsStore` abstraction allows:
    - Switching from in-memory to persistent or distributed implementations (e.g., Redis, database).
- Event ingestion can be replaced with:
    - Real message queues (Kafka, RabbitMQ, etc.) instead of the mock generator.

---

## Technologies Used

- Java 11
- Spring Boot (Web)
- Lombok
- In-memory data structures for metrics