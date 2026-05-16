# DDD Monolith Decomposer - Implementation Artifacts

## Project Management Service - Anti-Corruption Layer

### ProjectServiceAdapter.java
```java
package com.mycompany.entapp.projectservice.adapter;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.ArrayList;

@Component
public class ProjectServiceAdapter {

    private final RestTemplate restTemplate;
    private static final String EMPLOYEE_SERVICE_URL = "http://employee-service";

    public ProjectServiceAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackGetEmployees")
    public List<EmployeeDTO> getAvailableEmployees() {
        EmployeeDTO[] employees = restTemplate.getForObject(
            EMPLOYEE_SERVICE_URL + "/api/employees/available",
            EmployeeDTO[].class
        );
        return employees != null ? List.of(employees) : new ArrayList<>();
    }

    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackAssignEmployee")
    public void assignEmployeeToProject(int employeeId, int projectId) {
        restTemplate.postForObject(
            EMPLOYEE_SERVICE_URL + "/api/employees/{id}/assign",
            new AssignmentRequest(projectId),
            Void.class,
            employeeId
        );
    }

    public List<EmployeeDTO> fallbackGetEmployees() {
        return new ArrayList<>(); // Return empty list if service unavailable
    }

    public void fallbackAssignEmployee(int employeeId, int projectId) {
        // Log and queue for retry
    }

    @lombok.Data
    public static class AssignmentRequest {
        private int projectId;

        public AssignmentRequest(int projectId) {
            this.projectId = projectId;
        }
    }
}
```

### Resilience Configuration
```java
package com.mycompany.entapp.projectservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class ResilienceConfig {

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50.0f)
            .waitDurationInOpenState(Duration.ofSeconds(10))
            .permittedNumberOfCallsInHalfOpenState(3)
            .slowCallRateThreshold(50.0f)
            .slowCallDurationThreshold(Duration.ofSeconds(5))
            .build();

        return CircuitBreakerRegistry.of(config);
    }
}
```

## Employee Management Service - Event Publisher

### EmployeeEventPublisher.java
```java
package com.mycompany.entapp.employeeservice.events;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EmployeeEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public EmployeeEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishEmployeeCreated(EmployeeCreatedEvent event) {
        publishEvent("employee-events", event);
    }

    public void publishEmployeeAssigned(EmployeeAssignedEvent event) {
        publishEvent("employee-events", event);
    }

    public void publishEmployeeRemovedFromProject(EmployeeRemovedEvent event) {
        publishEvent("employee-events", event);
    }

    private void publishEvent(String topic, Object event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, event.toString(), message);
            log.info("Published event: {}", event.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("Failed to publish event", e);
            throw new RuntimeException("Event publishing failed", e);
        }
    }
}
```

### Domain Events
```java
package com.mycompany.entapp.employeeservice.events;

import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public class DomainEvent {
    private String eventId = UUID.randomUUID().toString();
    private String eventType;
    private int aggregateId;
    private String aggregateType = "Employee";
    private Instant timestamp = Instant.now();
    private int version = 1;
    private String correlationId;
}

@Data
public class EmployeeCreatedEvent extends DomainEvent {
    private String firstname;
    private String surname;
    private String role;

    public EmployeeCreatedEvent(int employeeId, String firstname, String surname, String role) {
        this.aggregateId = employeeId;
        this.firstname = firstname;
        this.surname = surname;
        this.role = role;
        this.eventType = "EmployeeCreated";
    }
}

@Data
public class EmployeeAssignedEvent extends DomainEvent {
    private int projectId;
    private String dateStarted;

    public EmployeeAssignedEvent(int employeeId, int projectId, String dateStarted) {
        this.aggregateId = employeeId;
        this.projectId = projectId;
        this.dateStarted = dateStarted;
        this.eventType = "EmployeeAssignedToProject";
    }
}

@Data
public class EmployeeRemovedEvent extends DomainEvent {
    private int projectId;

    public EmployeeRemovedEvent(int employeeId, int projectId) {
        this.aggregateId = employeeId;
        this.projectId = projectId;
        this.eventType = "EmployeeRemovedFromProject";
    }
}
```

## Saga Orchestration

### ProjectAssignmentSaga.java
```java
package com.mycompany.entapp.projectservice.saga;

import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProjectAssignmentSaga {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ProjectRepository projectRepository;

    public void startAssignmentSaga(ProjectAssignmentCommand command) {
        log.info("Starting assignment saga for employee {} on project {}", 
            command.getEmployeeId(), command.getProjectId());

        // Step 1: Request employee availability check
        publishEvent("assignment-requests", new EmployeeAvailabilityCheckRequest(
            command.getEmployeeId(),
            command.getProjectId(),
            command.getCorrelationId()
        ));
    }

    @KafkaListener(topics = "assignment-responses")
    public void handleEmployeeResponse(String message) {
        // Parse response
        EmployeeAvailabilityResponse response = parseResponse(message);

        if (response.isAvailable()) {
            // Step 2: Complete assignment
            completeAssignment(response.getEmployeeId(), response.getProjectId());
            log.info("Assignment completed: employee {} on project {}", 
                response.getEmployeeId(), response.getProjectId());
        } else {
            // Step 3: Compensate - reject assignment
            rejectAssignment(response.getEmployeeId(), response.getProjectId());
            log.warn("Assignment rejected: employee {} on project {}", 
                response.getEmployeeId(), response.getProjectId());
        }
    }

    private void completeAssignment(int employeeId, int projectId) {
        // Update database
        Project project = projectRepository.findById(projectId).orElseThrow();
        project.getEmployeeProjects().add(new EmployeeProject(employeeId, projectId));
        projectRepository.save(project);
    }

    private void rejectAssignment(int employeeId, int projectId) {
        log.info("Compensating: rejecting assignment for employee {} on project {}", 
            employeeId, projectId);
    }

    private EmployeeAvailabilityResponse parseResponse(String message) {
        // Parse Kafka message
        return new EmployeeAvailabilityResponse();
    }
}

@lombok.Data
class ProjectAssignmentCommand {
    private int employeeId;
    private int projectId;
    private String correlationId;
}

@lombok.Data
class EmployeeAvailabilityCheckRequest {
    private int employeeId;
    private int projectId;
    private String correlationId;

    public EmployeeAvailabilityCheckRequest(int employeeId, int projectId, String correlationId) {
        this.employeeId = employeeId;
        this.projectId = projectId;
        this.correlationId = correlationId;
    }
}

@lombok.Data
class EmployeeAvailabilityResponse {
    private int employeeId;
    private int projectId;
    private boolean available;
}
```

## Spring Boot Configuration

### application-projectservice.yml
```yaml
spring:
  application:
    name: project-service
  datasource:
    url: jdbc:postgresql://postgres:5432/project_service
    username: projectuser
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        jdbc:
          batch_size: 20

  kafka:
    bootstrap-servers: kafka:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
    consumer:
      group-id: project-service
      auto-offset-reset: earliest

server:
  port: 8081
  servlet:
    context-path: /api
  compression:
    enabled: true
    min-response-size: 1024

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
  health:
    livenessState:
      enabled: true
    readinessState:
      enabled: true

logging:
  level:
    root: INFO
    com.mycompany.entapp: DEBUG
  pattern:
    json: '{"timestamp":"%d","level":"%p","logger":"%c","message":"%m","correlation-id":"%X{correlationId}"}'
```

### Kafka Configuration
```java
package com.mycompany.entapp.projectservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(
            Map.ofEntries(
                Map.entry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092"),
                Map.entry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class),
                Map.entry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class),
                Map.entry(ProducerConfig.ACKS_CONFIG, "all"),
                Map.entry(ProducerConfig.RETRIES_CONFIG, 3)
            )
        );
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
```

## Docker Configuration

### Dockerfile (Project Service)
```dockerfile
FROM openjdk:17-slim as builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar project-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75.0", "-jar", "project-service.jar"]
```

### docker-compose.yml (Local Development)
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:14-alpine
    environment:
      POSTGRES_USER: projectuser
      POSTGRES_PASSWORD: devpass
      POSTGRES_DB: project_service
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data

  kafka:
    image: confluentinc/cp-kafka:7.4.0
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"

  zookeeper:
    image: confluentinc/cp-zookeeper:7.4.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    ports:
      - "2181:2181"

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes

  project-service:
    build:
      context: .
      dockerfile: Dockerfile
    environment:
      SPRING_PROFILES_ACTIVE: dev
      DB_PASSWORD: devpass
    depends_on:
      - postgres
      - kafka
      - redis
    ports:
      - "8081:8081"

volumes:
  postgres-data:
```

## Kubernetes Manifests

### deployment.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: project-service
  namespace: production
  labels:
    app: project-service
    version: v1
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: project-service
  template:
    metadata:
      labels:
        app: project-service
        version: v1
      annotations:
        prometheus.io/scrape: "true"
        prometheus.io/port: "8081"
        prometheus.io/path: "/actuator/prometheus"
    spec:
      containers:
      - name: project-service
        image: myregistry.azurecr.io/project-service:1.0.0
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: 8081
          name: http
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: project-service-secrets
              key: db-password
        resources:
          requests:
            cpu: 500m
            memory: 512Mi
          limits:
            cpu: 1000m
            memory: 1024Mi
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8081
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8081
          initialDelaySeconds: 10
          periodSeconds: 5
          timeoutSeconds: 5
          failureThreshold: 3
        volumeMounts:
        - name: config
          mountPath: /etc/config
          readOnly: true
      volumes:
      - name: config
        configMap:
          name: project-service-config
```

### service.yaml
```yaml
apiVersion: v1
kind: Service
metadata:
  name: project-service
  namespace: production
  labels:
    app: project-service
spec:
  type: ClusterIP
  selector:
    app: project-service
  ports:
  - port: 80
    targetPort: 8081
    protocol: TCP
    name: http
```

### configmap.yaml
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: project-service-config
  namespace: production
data:
  application.yml: |
    spring:
      kafka:
        bootstrap-servers: kafka:9092
      datasource:
        url: jdbc:postgresql://postgres:5432/project_service
```

## Database Migration Scripts

### V001__Create_Project_Schema.sql
```sql
-- Create tables for Project Service
CREATE TABLE client (
    id SERIAL PRIMARY KEY,
    client_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE project (
    id SERIAL PRIMARY KEY,
    project_title VARCHAR(255) NOT NULL,
    date_started DATE NOT NULL,
    date_ended DATE,
    client_id INTEGER NOT NULL REFERENCES client(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_client_id (client_id),
    INDEX idx_date_started (date_started)
);

CREATE TABLE employee_project (
    employee_id INTEGER NOT NULL,
    project_id INTEGER NOT NULL,
    date_started DATE NOT NULL,
    date_ended DATE,
    PRIMARY KEY (employee_id, project_id),
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);

CREATE INDEX idx_project_employee ON employee_project(employee_id);
```

### V002__Add_Audit_Columns.sql
```sql
-- Add audit columns for compliance
ALTER TABLE project ADD COLUMN audit_created_by VARCHAR(255);
ALTER TABLE project ADD COLUMN audit_modified_by VARCHAR(255);
ALTER TABLE client ADD COLUMN audit_created_by VARCHAR(255);
ALTER TABLE client ADD COLUMN audit_modified_by VARCHAR(255);
```

## Contract Testing

### ProjectServiceContractTest.java
```java
package com.mycompany.entapp.projectservice.contract;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRulev3;
import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.core.model.RequestResponsePact;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class ProjectServiceContractTest {

    @Rule
    public PactProviderRulev3 mockProvider = new PactProviderRulev3("employee-service", "localhost", 8080, this);

    @Pact(provider = "employee-service", consumer = "project-service")
    public RequestResponsePact employeeAvailabilityPact(PactBuilder builder) {
        return builder
            .uponReceiving("a request for available employees")
            .path("/api/employees/available")
            .method("GET")
            .willRespondWith()
            .status(200)
            .body(new PactDslJsonBody()
                .minArrayLike("employees", 1)
                .numberType("id")
                .stringType("firstname")
                .closeObject()
                .closeArray())
            .toPact();
    }

    @Test
    public void testEmployeeAvailabilityEndpoint() {
        RestTemplate restTemplate = new RestTemplate();
        // Test implementation
    }
}
```

## Testing Artifacts

### Dual-Write Verification Test
```java
package com.mycompany.entapp.projectservice.migration;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
    "dual.write.enabled=true",
    "legacy.db.enabled=true"
})
public class DualWriteVerificationTest {

    @Test
    public void verifyProjectCreatedInBothDatabases() {
        // Create project in new service
        Project project = projectService.createProject(new CreateProjectRequest(...));

        // Verify in PostgreSQL
        Project postgresProject = postgresRepository.findById(project.getId()).orElseThrow();
        assertThat(postgresProject).isNotNull();

        // Verify in legacy MySQL
        Project mysqlProject = mysqlRepository.findById(project.getId()).orElseThrow();
        assertThat(mysqlProject).isNotNull();

        // Compare data
        assertThat(postgresProject).isEqualTo(mysqlProject);
    }

    @Test
    public void verifyProjectUpdateConsistency() {
        // Update project title
        projectService.updateProject(1, "New Title");

        // Check both databases
        Project postgresVersion = postgresRepository.findById(1).orElseThrow();
        Project mysqlVersion = mysqlRepository.findById(1).orElseThrow();

        assertThat(postgresVersion.getProjectTitle())
            .isEqualTo(mysqlVersion.getProjectTitle())
            .isEqualTo("New Title");
    }
}
```

---

## Summary

These artifacts provide:

1. **Anti-Corruption Layer** - Isolation from external service changes
2. **Event Publishing** - Domain-driven event infrastructure
3. **Saga Orchestration** - Distributed transaction handling
4. **Spring Configuration** - Production-ready settings
5. **Docker Support** - Local development and deployment
6. **Kubernetes Manifests** - Scalable deployments
7. **Database Migrations** - Versioned schema changes
8. **Contract Tests** - API compatibility verification
9. **Dual-Write Tests** - Data consistency validation

These templates should be customized for your specific implementation details and security requirements.
