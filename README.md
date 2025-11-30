# Analytics Service

A simple real-time analytics demo application built with Java 11 and Spring Boot.  
It ingests user events, processes them in real time, and exposes metrics over REST APIs, plus a simple HTML dashboard.

---

## Features

- Real-time ingestion and processing of user events
- In-memory storage of analytics metrics
- Active user count for a configurable time window
- Top visited pages over a recent time window
- Active sessions per user
- Simple mock event generator to quickly populate data
- Lightweight HTML dashboard UI (`dashboard.html`) to visualize metrics

---

## High-Level Architecture

- **Controller Layer**
    - Exposes REST endpoints under `/dashboard`.
    - Delegates to a dashboard service to fetch metrics.

- **Service Layer**
    - `DashboardService` and its implementation aggregate and return metrics.
    - `EventIngestionService` accepts raw events and passes them to the processor.
    - `RealTimeEventProcessor` updates metrics in the `MetricsStore`.
    - `MockEventGeneratorRunner` manages the lifecycle of the mock event generator (start/stop/pause/resume) that feeds events into `EventIngestionService`.

- **Metrics Store**
    - `MetricsStore` interface abstracts metric read/write operations.
    - `InMemoryMetricsStore` (implementation) keeps counters and windows in memory.

- **Models**
    - `ActiveUserMetric` – holds the active user count.
    - `PageViewMetric` – represents page URL and view count.
    - `UserSessionMetric` – represents active sessions for a given user.

- **Mock Event Generator**
    - `MockEventGenerator` periodically creates random user events.
    - `DefaultMockEventGenerator` is a concrete implementation that generates realistic demo traffic.
    - `MockEventGeneratorRunner` creates and starts the generator so events continuously flow into the system.
    - Useful for demos and local testing without external traffic.

- **Dashboard UI**
    - `dashboard.html` is a simple browser-based dashboard.
    - It calls the REST endpoints under `/dashboard` and visualizes:
        - Active users
        - Top pages
        - Active sessions for a given user

---

## REST Endpoints

Base path: `/dashboard`

1. `GET /dashboard/active-users`
    - Returns `ActiveUserMetric`.
    - Represents number of active users in the last 5 minutes.
 
2. `GET /dashboard/top-pages`
    - Returns a list of `PageViewMetric`.
    - Top N pages (e.g., 5) in the last 15 minutes.

3. `GET /dashboard/users/{userId}/active-sessions`
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
5. The dashboard endpoints return these objects as JSON for the REST API and for consumption by `dashboard.html`.

---

## Running the Application

1. Ensure you have:
    - Java 11+
    - Gradle (wrapper is recommended)

2. Build and run:

   ```bash
   # Using Gradle wrapper
   ./gradlew clean build
   ./gradlew bootRun

   ```

   On Windows:

   ```bash
   gradlew.bat clean build
   gradlew.bat bootRun
   ```
Or run the fat JAR directly:

  ```bash
      ./gradlew clean build
      java -jar build/libs/analytics-service-1.0.0-SNAPSHOT-all.jar
 ```

3. Access endpoints and UI:

    - HTML dashboard:  
      `http://localhost:8080/dashboard.html`

    - Active users (JSON):  
      `GET http://localhost:8080/dashboard/active-users`

    - Top pages (JSON):  
      `GET http://localhost:8080/dashboard/top-pages`

    - Active sessions for a user (JSON):  
      `GET http://localhost:8080/dashboard/users/{userId}/active-sessions`

---

## Real-Time Dashboard Behavior

- When you start the application, the mock event generator begins producing events automatically, typically every second.
- These events are continuously ingested and processed to update the in-memory analytics metrics.
- To view the real-time metrics, open the dashboard in your browser:

    - URL: `http://localhost:8080/dashboard.html`

- The **Realtime Dashboard** loaded from this URL will display live data such as:
    - Active users
    - Top visited pages
    - Active sessions per user

As the background event generator keeps running, the numbers and charts 
on the Realtime Dashboard will update continuously without requiring you 
to manually refresh the data source.


## Architecture Choices

- Layered design: controller → service → repository to keep HTTP concerns, business logic, and data access separated.
- In-memory metrics store for simplicity and fast iteration, with an interface to allow swapping in a persistent store later.
- REST endpoints exposing aggregated metrics (active users, top pages, active sessions) for easy integration with dashboards or UIs.
- Separate mock event generator runner to simulate load without external dependencies.

## Data Modelling

- **Active users**: map of `userId → lastSeenTimestamp`.
- **Page views**: counters per page URL over a rolling time window.
- **Sessions**: map of `(userId, sessionId) → lastSeenTimestamp` to calculate active sessions per user.

---

## Testing

- Unit tests are provided for:
    - Dashboard REST endpoints.
    - Mock event generator lifecycle logic.

Run tests with command **./gradlew test**

---

## Performance Considerations

- All structures kept in memory for low‑latency reads and writes.
- Concurrent collections used to support multi‑threaded access.
- Periodic cleanup of old timestamps when querying to keep memory bounded and queries scoped to the configured time window.
- Current implementation is suitable for demo / low volume usage; for higher scale, you can:
    - Move metrics to a dedicated store (time‑series DB, key‑value store, or cache like Redis).
    - Pre‑aggregate counters to avoid scanning long timestamp lists.
    - Introduce sharding or partitioning by user or page to reduce contention.

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