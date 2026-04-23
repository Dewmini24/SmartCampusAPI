# SmartCampusAPI — 5COSC022C Coursework

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

## Key Features
- RESTful API design
- Sub-resource locator pattern
- Thread-safe in-memory storage
- Structured error responses
- Centralized logging
- Proper HTTP status codes

## Conclusion

This project demonstrates a complete RESTful API using JAX-RS, following best practices in backend development. It includes proper validation, error handling, and scalable architecture, reflecting real-world API design.
