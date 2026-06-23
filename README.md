# Event Ledger

A production-ready event processing platform built using Spring Boot microservices. The system processes financial events, updates account balances, maintains transaction history, and demonstrates modern engineering practices including resiliency, observability, testing, and clean architecture.

## Repository

GitHub: https://github.com/ravipatidarr/event-ledger

---

# Solution Overview

The solution consists of two independently deployable microservices:

### Event Gateway

Responsible for:

* Event ingestion
* Request validation
* Event persistence
* Idempotency enforcement
* Calling downstream Account Service

### Account Service

Responsible for:

* Account balance management
* Transaction processing
* Transaction history maintenance
* Account information retrieval

---

# Architecture

```text
                        +----------------+
                        |     Client     |
                        +--------+-------+
                                 |
                                 v
                    +------------------------+
                    |     Event Gateway      |
                    |         :8080          |
                    +-----------+------------+
                                |
                                | OpenFeign
                                |
                                v
                    +------------------------+
                    |     Account Service    |
                    |         :8081          |
                    +-----------+------------+
                                |
                                v
                          +-----------+
                          | H2 DB     |
                          +-----------+
```

---

# Technology Stack

| Category      | Technology                   |
| ------------- | ---------------------------- |
| Language      | Java 21                      |
| Framework     | Spring Boot 3.5.4            |
| Persistence   | Spring Data JPA              |
| Database      | H2 Database                  |
| Communication | OpenFeign                    |
| Resiliency    | Resilience4j                 |
| Monitoring    | Spring Boot Actuator         |
| Metrics       | Micrometer + Prometheus      |
| Logging       | Logback + Structured Logging |
| Testing       | JUnit 5, Mockito             |
| Build Tool    | Maven                        |

---

# Key Features

## Event Processing

* Create events
* Retrieve event details
* Retrieve account events
* Maintain transaction history

## Idempotency

Duplicate events are automatically detected using unique Event IDs.

Repeated submissions do not create duplicate records or duplicate balance updates.

## Validation

Input validation using Jakarta Bean Validation.

Examples:

* Mandatory fields validation
* Positive amount validation
* Invalid request handling

## Global Exception Handling

Centralized exception management using Spring Controller Advice.

Consistent API error responses across services.

## Resiliency

Implemented using Resilience4j Circuit Breaker.

Features:

* Automatic failure detection
* Service isolation
* Graceful degradation
* Fallback responses

Example fallback response:

```json
{
  "eventId": "evt-900",
  "accountId": "acct-123",
  "status": "SERVICE_UNAVAILABLE"
}
```

---

# Observability

The application includes production-grade observability features.

## Health Monitoring

```http
GET /actuator/health
```

## Metrics

```http
GET /actuator/metrics
```

## Prometheus Metrics

```http
GET /actuator/prometheus
```

## Distributed Request Tracking

Every request receives a Trace ID.

```text
X-Trace-Id
```

Trace IDs are propagated between services and included in application logs.

## Structured Logging

Logs include:

* Trace ID
* Timestamp
* Log Level
* Service Information
* Request Details

---

# API Endpoints

## Event Gateway

### Create Event

```http
POST /events
```

Sample Request:

```json
{
  "eventId": "evt-100",
  "accountId": "acct-123",
  "type": "CREDIT",
  "amount": 500,
  "currency": "USD",
  "eventTimestamp": "2026-06-23T12:00:00Z"
}
```

### Get Event

```http
GET /events/{eventId}
```

### Get Account Events

```http
GET /accounts/{accountId}/events
```

---

## Account Service

### Get Balance

```http
GET /accounts/{accountId}/balance
```

### Get Transactions

```http
GET /accounts/{accountId}/transactions
```

---

# Running the Application

## Start Account Service

```bash
cd account-service
mvnw.cmd spring-boot:run
```

## Start Event Gateway

```bash
cd event-gateway
mvnw.cmd spring-boot:run
```

---

# Testing

Unit tests implemented using:

* JUnit 5
* Mockito

Coverage includes:

* Event Service
* Account Service
* Application Context Validation

Run tests:

```bash
mvnw.cmd test
```

---

# Future Improvements

* Docker Support
* Docker Compose
* PostgreSQL
* Kafka Integration
* OpenAPI / Swagger
* Distributed Tracing (Zipkin/OpenTelemetry)
* CI/CD Pipeline
* Kubernetes Deployment

---

# Author

Ravi Patidar
Java Full Stack Developer with GenAI
