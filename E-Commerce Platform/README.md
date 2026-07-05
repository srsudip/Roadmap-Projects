# Scalable E-Commerce Platform

A microservices-based e-commerce platform built with Spring Boot, Spring Cloud, and Docker — following the [roadmap.sh Scalable E-Commerce Platform](https://roadmap.sh/projects/ecommerce-platform) project specification.

## Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                        API Gateway (:8080)                        │
│                   Spring Cloud Gateway + CORS                     │
└──────────┬──────────┬──────────┬──────────┬──────────┬──────────┘
           │          │          │          │          │
     ┌─────▼──┐ ┌─────▼──┐ ┌────▼───┐ ┌───▼────┐ ┌──▼─────────┐
     │  User  │ │Product │ │  Cart  │ │ Order  │ │  Payment   │
     │Service │ │Service │ │Service │ │Service │ │  Service   │
     │ :8081  │ │ :8082  │ │ :8083  │ │ :8084  │ │   :8085    │
     └───┬────┘ └───┬────┘ └───┬────┘ └───┬────┘ └─────┬──────┘
         │          │          │          │             │
    ┌────▼───┐ ┌───▼────┐ ┌──▼─────┐ ┌──▼─────┐ ┌────▼─────┐
    │Postgres│ │Postgres│ │Postgres│ │Postgres│ │ Postgres  │
    │ users  │ │products│ │ carts  │ │ orders │ │ payments  │
    │ :5433  │ │ :5434  │ │ :5435  │ │ :5436  │ │  :5437    │
    └────────┘ └────────┘ └────────┘ └────────┘ └──────────┘

              ┌─────────────────┐     ┌──────────────┐
              │  Notification   │◄────┤   RabbitMQ   │
              │   Service       │     │  :5672/:15672│
              │    :8086        │     └──────────────┘
              └────────┬────────┘
                  ┌────▼─────┐
                  │ Postgres  │
                  │notifications│
                  │   :5438   │
                  └──────────┘

              ┌─────────────────┐
              │  Eureka Server  │  (Service Discovery)
              │     :8761       │
              └─────────────────┘
```

## Services

| Service | Port | Database | Description |
|---------|------|----------|-------------|
| **Eureka Server** | 8761 | — | Service discovery & registration |
| **API Gateway** | 8080 | — | Routing, CORS, load balancing |
| **User Service** | 8081 | users | Authentication, JWT, user profiles |
| **Product Service** | 8082 | products | Product catalog, categories, search |
| **Cart Service** | 8083 | carts | Shopping cart management |
| **Order Service** | 8084 | orders | Order processing & tracking |
| **Payment Service** | 8085 | payments | Payment processing & refunds |
| **Notification Service** | 8086 | notifications | Email/SMS via RabbitMQ |

## Tech Stack

- **Java 21** + **Spring Boot 3.2.5**
- **Spring Cloud 2023.0.1** (Eureka, Gateway, Feign)
- **PostgreSQL 16** (one per service)
- **RabbitMQ 3** (async notification events)
- **JWT** (jjwt 0.12.5) for authentication
- **BCrypt** for password hashing
- **Docker Compose** for orchestration

## Prerequisites

- Java 21+
- Maven 3.9+
- Docker + Docker Compose (or Colima on macOS)

## Quick Start

```bash
# Clone and start everything
git clone https://github.com/srsudip/Roadmap-Projects.git
cd "Roadmap-Projects/E-Commerce Platform"
docker compose up -d --build
```

All services start in ~60 seconds. Check health:
```bash
docker compose ps
```

## API Endpoints

All requests go through the **API Gateway** at `http://localhost:8080`.

### Authentication
```bash
# Register
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"pass123","fullName":"John Doe"}'

# Login
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"pass123"}'
```

### Products
```bash
# List all products
curl http://localhost:8080/api/products/

# Search by name
curl "http://localhost:8080/api/products/search?name=laptop"

# Filter by category
curl http://localhost:8080/api/products/category/1
```

### Shopping Cart
```bash
# Add to cart
curl -X POST http://localhost:8080/api/cart/1/items \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":2}'

# View cart
curl http://localhost:8080/api/cart/1
```

### Orders
```bash
# Create order
curl -X POST http://localhost:8080/api/orders/ \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"shippingAddress":"123 Main St"}'

# View order
curl http://localhost:8080/api/orders/1

# Cancel order
curl -X PUT http://localhost:8080/api/orders/1/cancel
```

### Payments
```bash
# Process payment
curl -X POST http://localhost:8080/api/payments/ \
  -H "Content-Type: application/json" \
  -d '{"orderId":1,"userId":1,"amount":99.99,"method":"CREDIT_CARD"}'

# Refund
curl -X PUT http://localhost:8080/api/payments/1/refund
```

## Service Details

### User Service
- JWT-based authentication (24h expiry)
- Roles: `USER`, `ADMIN`
- Endpoints: register, login, get/update/delete users

### Product Service
- Categories with products
- Search by name, filter by category
- Stock management

### Cart Service
- Per-user shopping cart
- Add/update/remove items with quantity merge

### Order Service
- Order lifecycle: `PENDING → SHIPPED → DELIVERED` (or `CANCELLED`)
- Calculates total from cart items

### Payment Service
- Simulated payment gateway (UUID transaction IDs)
- Supports: Stripe, PayPal, Credit Card, Debit Card
- Refund capability

### Notification Service
- RabbitMQ-driven async notifications
- Event types: ORDER_CONFIRMATION, PAYMENT_RECEIVED, ORDER_SHIPPED, ORDER_DELIVERED
- Simulated email/SMS channels

## Stopping

```bash
docker compose down
```

## Project Structure

```
E-Commerce Platform/
├── pom.xml                          # Parent POM (multi-module)
├── docker-compose.yml               # Full stack orchestration
├── eureka-server/                   # Service discovery
├── api-gateway/                     # API routing
├── user-service/                    # Auth & user management
├── product-service/                 # Product catalog
├── cart-service/                    # Shopping cart
├── order-service/                   # Order processing
├── payment-service/                 # Payment handling
└── notification-service/            # Async notifications
```
