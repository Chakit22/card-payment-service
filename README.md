# Card Payment Service

A Spring Boot microservice simulating card payment processing.

## Tech Stack

- Java 17
- Spring Boot 2.7
- Spring Data JPA
- H2 in-memory database
- SpringDoc OpenAPI (Swagger)
- Docker

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/transactions` | Create a payment (auto-approved if amount < 10,000) |
| GET | `/api/transactions/{id}` | Get transaction by ID |
| GET | `/api/transactions?page=0&size=20` | List all transactions (paginated) |
| POST | `/api/transactions/{id}/refund` | Refund a transaction |
| GET | `/api/merchants/{merchantId}/transactions` | Get transactions by merchant |

## Run Locally

```bash
mvn spring-boot:run
```

API available at `http://localhost:8080/api/transactions`
Swagger UI at `http://localhost:8080/swagger-ui.html`

## Docker

```bash
mvn clean package -DskipTests
docker build -t card-payment-service .
docker run -p 8080:8080 card-payment-service
```

## Run Tests

```bash
mvn test
```
