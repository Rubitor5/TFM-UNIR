# Project Management Microservice

Extracted Project Management Context from the Enterprise Application monolith as a standalone microservice.

## Architecture

This microservice implements the layered architecture pattern:

```
┌─────────────────────────────────────┐
│   REST Endpoints (Controllers)      │
│  - /api/projects                    │
│  - /api/clients                     │
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│   Services (Business Logic)         │
│  - ProjectService                   │
│  - ClientService                    │
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│   Repositories (Mock Data)          │
│  - ProjectRepository                │
│  - ClientRepository                 │
└─────────────────────────────────────┘
```

## Module Structure

```
project-microservice/
├── src/main/java/com/mycompany/projectms/
│   ├── ProjectMicroserviceApplication.java    (Spring Boot entry point)
│   ├── domain/
│   │   ├── model/
│   │   │   ├── Project.java
│   │   │   └── Client.java
│   │   ├── service/
│   │   │   ├── ProjectService.java
│   │   │   ├── ClientService.java
│   │   │   ├── impl/
│   │   │   │   ├── ProjectServiceImpl.java
│   │   │   │   └── ClientServiceImpl.java
│   │   └── repository/
│   │       ├── ProjectRepository.java
│   │       └── ClientRepository.java
│   └── infrastructure/
│       ├── rest/
│       │   ├── endpoint/
│       │   │   ├── ProjectRestEndpoint.java
│       │   │   └── ClientRestEndpoint.java
│       │   ├── resource/
│       │   │   ├── ProjectResource.java
│       │   │   └── ClientResource.java
│       │   └── mapper/
│       │       ├── ProjectResourceMapper.java
│       │       └── ClientResourceMapper.java
│       └── db/repository/
│           ├── MockProjectRepository.java
│           └── MockClientRepository.java
├── src/main/resources/
│   └── application.yml
├── pom.xml
└── README.md
```

## Features

### Endpoints

#### Projects API
- **GET /api/projects** - Retrieve all projects
- **GET /api/projects/{projectId}** - Get project by ID
- **POST /api/projects** - Create new project
- **PUT /api/projects/{projectId}** - Update project
- **DELETE /api/projects/{projectId}** - Delete project

#### Clients API
- **GET /api/clients** - Retrieve all clients
- **GET /api/clients/{clientId}** - Get client by ID
- **POST /api/clients** - Create new client
- **PUT /api/clients/{clientId}** - Update client
- **DELETE /api/clients/{clientId}** - Delete client

### Mock Data

The microservice includes pre-loaded mock data:

**Clients:**
- 1: Acme Corporation
- 2: Tech Innovations Inc
- 3: Global Solutions Ltd

**Projects:**
- 1: Mobile App Development (Acme Corporation)
- 2: Cloud Migration (Tech Innovations Inc)
- 3: Data Analytics Platform (Global Solutions Ltd)

## Getting Started

### Prerequisites
- Java 11+
- Maven 3.6+

### Building

```bash
cd project-microservice
mvn clean package
```

### Running

```bash
mvn spring-boot:run
```

Or run the JAR directly:

```bash
java -jar target/project-microservice-1.0-SNAPSHOT.jar
```

The service will start on **http://localhost:8081**

## Testing the API

### Get All Projects
```bash
curl http://localhost:8081/api/projects
```

### Get Project by ID
```bash
curl http://localhost:8081/api/projects/1
```

### Create New Project
```bash
curl -X POST http://localhost:8081/api/projects \
  -H "Content-Type: application/json" \
  -d '{
    "projectTitle": "New Project",
    "dateStarted": "2026-05-16",
    "clientId": 1,
    "clientName": "Acme Corporation"
  }'
```

### Get All Clients
```bash
curl http://localhost:8081/api/clients
```

### Get Client by ID
```bash
curl http://localhost:8081/api/clients/1
```

## Integration with CI/CD

This microservice is designed for automated CI/CD pipeline testing. The mock data allows:
- ✅ API endpoint testing
- ✅ Contract testing
- ✅ Integration testing
- ✅ Load testing
- ✅ No external dependencies (no DB, messaging, or caching required)

## Technology Stack

- **Framework:** Spring Boot 2.7.14
- **Language:** Java 11
- **Build Tool:** Maven
- **Architecture Pattern:** Layered/Hexagonal Architecture with DDD principles

## Future Enhancements

For production use, this microservice would need:
1. Real database (PostgreSQL recommended per the report)
2. Entity validation
3. Exception handling strategy
4. Logging and monitoring
5. Authentication/Authorization
6. API versioning
7. Caching layer
8. Event-driven communication with other services

## Notes

- All data is in-memory and will be reset on service restart
- No persistence layer - designed for testing purposes
- Mock repositories contain hardcoded data for rapid API testing
- No external dependencies on messaging or database systems

## Author

Based on DDD Monolith Decomposer Report - Project Management Microservice Context extraction
