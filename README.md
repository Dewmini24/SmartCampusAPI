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

### Part 1.1 — JAX-RS Lifecycle
*(to be completed)*

### Part 1.2 — HATEOAS
*(to be completed)*

### Part 2.1 — ID-only vs Full Object
*(to be completed)*

### Part 2.2 — DELETE Idempotency
*(to be completed)*

### Part 3.1 — @Consumes Mismatch
*(to be completed)*

### Part 3.2 — QueryParam vs PathParam
*(to be completed)*

### Part 4.1 — Sub-Resource Locator
*(to be completed)*

### Part 5.1 — HTTP 422 vs 404
*(to be completed)*

### Part 5.2 — Stack Trace Security Risks
*(to be completed)*
