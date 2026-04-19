# SmartCampusAPI — 5COSC022W Coursework

## Overview
A JAX-RS RESTful API for managing Rooms and Sensors across a Smart Campus.
Built with Jersey 2.41 and Jackson JSON, deployed on Apache Tomcat.

## Tech Stack
- Java 11
- JAX-RS (Jersey 2.41)
- Jackson JSON
- Apache Maven
- Apache Tomcat 9+

## How to Build and Run

### Prerequisites
- JDK 11+
- Apache Maven
- Apache Tomcat 9+

### Steps
1. Clone the repository:
```
   git clone https://github.com/Dewmini24/SmartCampusAPI.git
```
2. Build the WAR file:
```
   mvn clean package
```
3. Copy `target/SmartCampusAPI.war` to Tomcat's `webapps/` folder
4. Start Tomcat and access: `http://localhost:8080/SmartCampusAPI/api/v1`

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/v1 | Discovery endpoint |
| GET | /api/v1/rooms | List all rooms |
| POST | /api/v1/rooms | Create a room |
| GET | /api/v1/rooms/{id} | Get room by ID |
| DELETE | /api/v1/rooms/{id} | Delete a room |
| GET | /api/v1/sensors | List all sensors |
| POST | /api/v1/sensors | Register a sensor |
| GET | /api/v1/sensors?type=CO2 | Filter sensors by type |
| GET | /api/v1/sensors/{id}/readings | Get sensor readings |
| POST | /api/v1/sensors/{id}/readings | Add a reading |

## Sample curl Commands

### 1. Create a Room
```
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"LIB-301\",\"name\":\"Library Quiet Study\",\"capacity\":50}"
```

### 2. Get All Rooms
```
curl http://localhost:8080/SmartCampusAPI/api/v1/rooms
```

### 3. Create a Sensor
```
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"TEMP-001\",\"type\":\"Temperature\",\"status\":\"ACTIVE\",\"currentValue\":22.5,\"roomId\":\"LIB-301\"}"
```

### 4. Filter Sensors by Type
```
curl http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=Temperature
```

### 5. Add a Sensor Reading
```
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"READ-001\",\"timestamp\":1714000000000,\"value\":23.1}"
```

## Report — Question Answers

### Part 1.1 — JAX-RS Resource Lifecycle & Thread Safety

By default, JAX-RS creates a **new instance of each resource class for every incoming HTTP request** (request-scoped lifecycle). This means that any instance variables declared inside a resource class are created fresh per request and cannot be used to store shared state between requests.

This architectural decision has a critical impact on in-memory data management. Since a new resource object is created per request, storing data inside the resource class would result in data loss after each request completes. To solve this, all shared data must be stored in a **separate singleton class** (DataStore) that lives for the entire lifetime of the application.

Furthermore, because multiple requests can arrive simultaneously, multiple resource instances will access the shared DataStore concurrently. To prevent race conditions and data corruption, `ConcurrentHashMap` is used instead of a regular `HashMap`. `ConcurrentHashMap` allows thread-safe reads and writes without requiring explicit `synchronized` blocks, ensuring data integrity under concurrent load.

---

### Part 1.2 — HATEOAS and Hypermedia-Driven APIs

HATEOAS (Hypermedia as the Engine of Application State) is considered a hallmark of advanced RESTful design because it makes APIs **self-describing and navigable**. Rather than requiring clients to hardcode URLs or rely on external documentation, the API itself provides links to related resources and available actions within each response.

For example, when a client calls `GET /api/v1`, the discovery endpoint returns links to `/api/v1/rooms` and `/api/v1/sensors`. The client can then navigate the entire API dynamically without any prior knowledge of the URL structure.

This benefits client developers significantly — they do not need to consult static documentation to discover endpoints, reducing coupling between client and server. If the server changes a URL, the client simply follows the new links provided in the response rather than breaking. This makes APIs more resilient, easier to evolve, and simpler to consume.

---

### Part 2.1 — ID-only vs Full Object in List Responses

When returning a list of rooms, there are two design options: returning only the IDs of each room, or returning the full room objects.

Returning **only IDs** reduces the response payload size significantly, which improves performance when the list is large. However, it forces the client to make additional requests to fetch details for each room (the N+1 problem), increasing total network round-trips and client-side complexity.

Returning **full room objects** increases the response payload but eliminates the need for follow-up requests. Clients receive all the data they need in a single call, reducing latency and simplifying client-side logic.

In this implementation, full room objects are returned in the list response. This is appropriate for a campus management system where clients typically need room details (name, capacity, sensor list) immediately, and the total number of rooms is manageable in size.

---

### Part 2.2 — DELETE Idempotency

The DELETE operation is **idempotent** in this implementation. Idempotency means that making the same request multiple times produces the same server state as making it once.

In this API, the first DELETE request for a room that exists (and has no sensors) will successfully remove it and return `204 No Content`. If the same DELETE request is sent again, the room no longer exists in the DataStore, so the server returns `404 Not Found`. The server state remains the same after both calls — the room is absent. No additional side effects occur on repeated calls.

This satisfies the REST definition of idempotency: the resource state on the server is identical regardless of how many times the DELETE is repeated. The only difference is the HTTP status code returned (204 on first call, 404 on subsequent calls), which is acceptable and expected behaviour.

---

### Part 3.1 — @Consumes Mismatch Behaviour

The `@Consumes(MediaType.APPLICATION_JSON)` annotation tells JAX-RS that the POST endpoint only accepts requests with a `Content-Type: application/json` header. If a client sends a request with a different content type such as `text/plain` or `application/xml`, JAX-RS will automatically reject the request before it even reaches the resource method.

Specifically, JAX-RS will return an **HTTP 415 Unsupported Media Type** response. This is handled by the framework's built-in message body reader mechanism — Jersey scans all registered `MessageBodyReader` implementations to find one that can deserialize the incoming content type. If no matching reader is found for the declared `@Consumes` type, the 415 error is returned automatically.

This protects the API from malformed or unexpected input formats and ensures that only properly structured JSON payloads are processed by the sensor registration logic.

---

### Part 3.2 — @QueryParam vs Path Parameter for Filtering

Using `@QueryParam` for filtering (e.g. `GET /api/v1/sensors?type=CO2`) is considered superior to embedding the filter in the URL path (e.g. `GET /api/v1/sensors/type/CO2`) for several reasons.

Query parameters are **optional by nature** — the same endpoint handles both the full list and the filtered list without requiring separate route definitions. Path parameters, by contrast, imply a mandatory part of the resource identity, making them semantically incorrect for optional filters.

Query parameters also support **multiple simultaneous filters** naturally (e.g. `?type=CO2&status=ACTIVE`), while path-based filtering becomes awkward and unreadable with multiple criteria. Additionally, REST conventions dictate that path segments should identify a resource, while query strings should refine or filter a collection. Using `@QueryParam` correctly communicates to client developers that the type filter is optional and does not change the fundamental resource being accessed.

---

### Part 4.1 — Sub-Resource Locator Pattern Benefits

The Sub-Resource Locator pattern allows a resource class to delegate handling of a nested URL to a separate, dedicated class. In this API, `SensorResource` delegates `/{sensorId}/readings` to a `SensorReadingResource` class rather than defining all reading-related endpoints directly inside `SensorResource`.

This approach provides significant architectural benefits in large APIs. Each class has a **single responsibility** — `SensorResource` manages sensors, while `SensorReadingResource` manages reading history. This separation makes the codebase easier to read, test, and maintain independently.

Without this pattern, a single controller class would grow to contain dozens of methods handling every nested path, making it difficult to navigate and increasing the risk of merge conflicts in team environments. Delegation to sub-resource classes mirrors the physical hierarchy of the campus (sensor → readings) and makes the URL structure self-documenting. It also allows each sub-resource class to be injected with only the context it needs (the sensorId), keeping method signatures clean.

---

### Part 5.1 — HTTP 422 vs 404 for Missing Referenced Resources

When a client submits a valid JSON payload for a new sensor but includes a `roomId` that does not exist, returning `404 Not Found` would be semantically misleading. A 404 typically indicates that the **requested URL** was not found — but in this case, the URL `/api/v1/sensors` is perfectly valid and was found successfully.

The actual problem is that the **content of the payload** references a resource (`roomId`) that does not exist. HTTP 422 Unprocessable Entity is more semantically accurate because it signals that the server understood the request format and content type, but was unable to process the instructions due to a logical or referential error within the payload itself.

Using 422 communicates clearly to client developers that the issue is not with the endpoint URL but with the data they provided, specifically the invalid foreign key reference. This distinction is important for API consumers to understand where to look when debugging errors.

---

### Part 5.2 — Security Risks of Exposing Stack Traces

Exposing raw Java stack traces to external API consumers presents serious cybersecurity risks. An attacker can extract several categories of sensitive information from a stack trace.

**Internal file paths** — Stack traces reveal the exact directory structure of the server, including package names and class file locations, helping attackers map the application's internal architecture.

**Framework and library versions** — Class names in the trace expose which frameworks and versions are in use (e.g. Jersey 2.41, Jackson). Attackers can cross-reference these versions against known CVE databases to identify exploitable vulnerabilities.

**Application logic** — The call stack reveals the exact sequence of method calls that led to the error, exposing business logic, validation flow, and internal class relationships that attackers can use to craft targeted exploits.

**Server environment details** — JDK version, operating system paths, and server configuration details may be visible, narrowing down the attack surface.

The Global ExceptionMapper intercepts all unhandled `Throwable` instances and returns a generic 500 response with no internal details, ensuring the API never leaks implementation information to external consumers regardless of what unexpected error occurs internally.
