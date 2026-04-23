# SmartCampusAPI — 5COSC022C.2 Client-Server Architectures Coursework

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

## Error Handling

Custom ExceptionMapper classes are used to return structured JSON responses:

Status	Scenario
409 Conflict	Room has active sensors
422 Unprocessable Entity	Invalid roomId
403 Forbidden	Sensor in maintenance
500 Internal Server Error	Unexpected errors

Example:

{
  "status": 409,
  "error": "Conflict",
  "message": "Room cannot be deleted while sensors exist"
}

## Logging

A custom logging filter is implemented using:

ContainerRequestFilter
ContainerResponseFilter

It logs:

HTTP request method and URI
HTTP response status codes

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
## Question Answers

### Part 1.1 — JAX-RS Resource Lifecycle & Thread Safety
By default, JAX-RS creates a new instance of each resource class for every 
incoming HTTP request (request-scoped lifecycle). This means instance variables 
are created fresh per request and cannot store shared state between requests.

To solve this, all shared data is stored in a separate singleton DataStore class 
that lives for the entire application lifetime. ConcurrentHashMap is used instead 
of HashMap to prevent race conditions when multiple requests access data simultaneously.

### Part 1.2 — HATEOAS
HATEOAS makes APIs self-describing by providing navigation links within responses. 
The discovery endpoint at GET /api/v1 returns links to /api/v1/rooms and /api/v1/sensors, 
allowing clients to navigate without hardcoding URLs or reading static documentation.

### Part 2.1 — ID-only vs Full Object
Returning only IDs reduces payload size but forces clients to make extra requests 
(N+1 problem). Returning full objects increases payload but gives clients all data 
in one call. This API returns full objects as campus management clients need room 
details immediately.

### Part 2.2 — DELETE Idempotency
DELETE is idempotent — the first call returns 204 No Content, repeated calls return 
404 Not Found. The server state is the same both times (room is absent), satisfying 
the REST definition of idempotency.

### Part 3.1 — @Consumes Mismatch
If a client sends text/plain instead of application/json, JAX-RS automatically returns 
415 Unsupported Media Type before the method is reached. Jersey scans MessageBodyReader 
implementations and rejects unmatched content types automatically.

### Part 3.2 — @QueryParam vs Path Parameter
Query parameters are optional by nature — GET /sensors handles both full list and 
filtered list in one endpoint. Path parameters imply mandatory resource identity. 
Query params also support multiple filters naturally (e.g. ?type=CO2&status=ACTIVE).

### Part 4.1 — Sub-Resource Locator Pattern
The pattern delegates nested URLs to dedicated classes. SensorResource delegates 
/{sensorId}/readings to SensorReadingResource. Each class has one responsibility, 
making the code easier to read, test and maintain. Without this, one massive controller 
would handle all paths.

### Part 5.1 — HTTP 422 vs 404
404 means the URL was not found. But /api/v1/sensors is a valid URL. The problem is 
inside the payload — the roomId does not exist. 422 Unprocessable Entity correctly 
signals that the server understood the request but could not process it due to invalid 
data inside the body.

### Part 5.2 — Stack Trace Security Risks
Stack traces expose internal file paths, framework versions (e.g. Jersey 2.41), 
application logic and server environment details. Attackers use this to find known 
CVE vulnerabilities. The GlobalExceptionMapper returns only a generic 500 message, 
hiding all internal details from external consumers.

## Key Features
- RESTful API design
- Sub-resource locator pattern
- Thread-safe in-memory storage
- Structured error responses
- Centralized logging
- Proper HTTP status codes

## Conclusion

This project demonstrates a complete RESTful API using JAX-RS, following best practices in backend development. It includes proper validation, error handling, and scalable architecture, reflecting real-world API design.
