# Scalable E-Commerce Platform

A microservices-based e-commerce platform built with Spring Boot, Spring Cloud, and Docker — following the [roadmap.sh Scalable E-Commerce Platform](https://roadmap.sh/projects/scalable-ecommerce-platform) project specification.

> **Quick start:** See [`RUNNER.md`](RUNNER.md) for step-by-step setup and run instructions.

## Architecture

```
                         ┌──────────────────┐
                         │  Frontend (:3000) │
                         │  Thymeleaf + CSS  │
                         └────────┬─────────┘
                                  │
                    ┌─────────────▼──────────────┐
                    │    API Gateway (:8080)       │
                    │  Spring Cloud Gateway + CORS │
                    └─────┬──────┬──────┬────┬────┘
                          │      │      │    │
                    ┌─────▼──┐ ┌▼────┐ ┌▼───▼──┐
                    │  User  │ │Cart │ │Order  │
                    │Service │ │Svc  │ │Service│
                    │ :8081  │ │:8083│ │ :8084 │
                    └───┬────┘ └──┬──┘ └───┬───┘
                   ┌────▼───┐ ┌──▼────┐ ┌──▼─────┐
                   │Postgres│ │Postgres│ │Postgres│
                   │ users  │ │ carts  │ │ orders │
                   │ :5433  │ │ :5435  │ │ :5436  │
                   └────────┘ └────────┘ └────────┘

              ┌───────────┐  ┌───────────┐  ┌──────────┐
              │  Product  │  │  Payment  │  │  Notif.  │
              │  Service  │  │  Service  │  │  Service │
              │  :8082    │  │  :8085    │  │  :8086   │
              └─────┬─────┘  └─────┬─────┘  └────┬─────┘
              ┌─────▼─────┐  ┌────▼──────┐  ┌───▼──────┐
              │ Postgres   │  │ Postgres  │  │ Postgres  │
              │ products   │  │ payments  │  │notif.     │
              │ :5434      │  │ :5437     │  │ :5438    │
              └────────────┘  └───────────┘  └──────────┘

         ┌──────────────┐     ┌─────────────────┐
         │   RabbitMQ   │────►│  Eureka Server   │
         │ :5672/:15672 │     │     :8761        │
         └──────────────┘     └─────────────────┘
```

## Project Structure

```
E-Commerce Platform/
├── Dockerfile                        # Multi-stage build (build → development → production)
├── docker-compose.yml                # Development: hot-reload, volume mounts
├── docker-compose-prod.yml           # Production: pre-built images, no build context
├── .env.development                  # Dev environment variables
├── .env.production                   # Prod environment variables (registry, tags)
├── .github/workflows/ci.yml         # GitHub Actions CI/CD pipeline
├── RUNNER.md                         # Step-by-step run instructions
├── database/                         # PostgreSQL init scripts
│   ├── init-userdb.sql
│   ├── init-productdb.sql
│   ├── init-cartdb.sql
│   ├── init-orderdb.sql
│   ├── init-paymentdb.sql
│   └── init-notificationdb.sql
├── backend/
│   ├── pom.xml                       # Multi-module parent POM (8 services)
│   ├── eureka-server/                # Service discovery
│   ├── api-gateway/                  # API routing
│   ├── user-service/                 # Auth & user management
│   ├── product-service/              # Product catalog (30 products)
│   ├── cart-service/                 # Shopping cart
│   ├── order-service/                # Order processing
│   ├── payment-service/              # Payment handling
│   └── notification-service/         # Async notifications
└── frontend/
    └── frontend-service/
        ├── pom.xml                   # Standalone Spring Boot
        └── src/                      # Thymeleaf web UI
```

## Services

| Service | Port | Database | Description |
|---------|------|----------|-------------|
| **Frontend** | 3000 | — | Thymeleaf web UI (browse, cart, checkout, orders, admin) |
| **Eureka Server** | 8761 | — | Service discovery & registration |
| **API Gateway** | 8080 | — | Routing, CORS, load balancing |
| **User Service** | 8081 | users | Authentication, JWT, user profiles |
| **Product Service** | 8082 | products | Product catalog (30 products, 5 categories) |
| **Cart Service** | 8083 | carts | Shopping cart management |
| **Order Service** | 8084 | orders | Order processing & tracking |
| **Payment Service** | 8085 | payments | Payment processing & refunds |
| **Notification Service** | 8086 | notifications | Email/SMS via RabbitMQ |

## Tech Stack

- **Java 21** + **Spring Boot 3.2.5**
- **Spring Cloud 2023.0.1** (Eureka, Gateway, Feign)
- **Thymeleaf** (server-rendered frontend)
- **PostgreSQL 16** (one database per service)
- **RabbitMQ 3** (async notification events)
- **JWT** (jjwt 0.12.5) for authentication
- **Docker** multi-stage builds (development + production targets)
- **Docker Compose** orchestration (dev + prod configurations)
- **GitHub Actions** CI/CD pipeline
- **Unsplash** for real product photography

## Docker Setup

| Stage | Purpose | Base Image |
|-------|---------|------------|
| `build` | Compile JAR with Maven | `maven:3.9-eclipse-temurin-21` |
| `development` | Hot-reload runtime | `eclipse-temurin:21-jre-alpine` |
| `production` | Minimal secure runtime | `eclipse-temurin:21-jre-alpine` + non-root user |

- **Dev:** `docker compose up -d --build` — volume mounts enable hot-reload
- **Prod:** `docker compose -f docker-compose-prod.yml up -d` — pre-built images, no build context

See [`RUNNER.md`](RUNNER.md) for full instructions.

## CI/CD Pipeline

GitHub Actions workflow (`.github/workflows/ci.yml`):

1. **Test** — Runs `mvn test` for backend and frontend in parallel
2. **Build** — Builds Docker images with `--target production` for all 9 services
3. **Push** — Pushes tagged images to GitHub Container Registry (GHCR)
4. **Deploy** — SSH into production server, pull images, restart containers

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
curl http://localhost:8080/api/products

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
  -d '{"productId":1,"productName":"Headphones","price":79.99,"quantity":2}'

# View cart
curl http://localhost:8080/api/cart/1
```

### Orders
```bash
# Create order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"shippingAddress":"123 Main St","items":[{"productId":1,"productName":"Headphones","price":79.99,"quantity":2}]}'

# View order
curl http://localhost:8080/api/orders/1

# Cancel order
curl -X PUT http://localhost:8080/api/orders/1/cancel
```

### Payments
```bash
# Process payment
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{"orderId":1,"userId":1,"amount":159.98,"method":"CREDIT_CARD"}'

# Refund
curl -X PUT http://localhost:8080/api/payments/1/refund
```

## Service Details

### User Service
- JWT-based authentication (24h expiry)
- Roles: `USER`, `ADMIN`
- Endpoints: register, login, get/update/delete users

### Product Service
- 30 products across 5 categories (Electronics, Clothing, Books, Home & Garden, Sports)
- Product images from Unsplash
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
# Development
docker compose down

# Production
docker compose -f docker-compose-prod.yml down
```

## Cleanup

```bash
# Remove all containers, images, and volumes
docker compose down --rmi all --volumes --remove-orphans
```
