# Scalable E-Commerce Platform

A full-stack microservices-based e-commerce platform built with Spring Boot, Spring Cloud, and Docker.

> Based on [roadmap.sh E-Commerce Platform Project](https://roadmap.sh/projects/ecommerce-platform)

## Architecture

```
┌─────────────┐
│ API Gateway  │ :8080
└──────┬──────┘
       │
       ├── User Service        :8081
       ├── Product Service     :8082
       ├── Cart Service        :8083
       ├── Order Service       :8084
       ├── Payment Service     :8085
       └── Notification Service :8086
```

| Component | Port | Description |
|-----------|------|-------------|
| Eureka Server | 8761 | Service discovery & registration |
| API Gateway | 8080 | Single entry point, routing, CORS |
| User Service | 8081 | Authentication (JWT), user profiles |
| Product Service | 8082 | Product catalog, categories, inventory |
| Cart Service | 8083 | Shopping cart management |
| Order Service | 8084 | Order placement, tracking, history |
| Payment Service | 8085 | Payment processing (Stripe/PayPal simulation) |
| Notification Service | 8086 | Email/SMS notifications via RabbitMQ |
| PostgreSQL | 5432 | Database per service |
| RabbitMQ | 5672 | Async messaging for notifications |

## Tech Stack

- **Framework:** Spring Boot 3.2.5
- **Cloud:** Spring Cloud 2023.0.1
- **Language:** Java 21
- **Database:** PostgreSQL 16 (one per service)
- **Messaging:** RabbitMQ
- **Discovery:** Eureka
- **Gateway:** Spring Cloud Gateway
- **Auth:** Spring Security + JWT
- **Build:** Maven
- **Containerization:** Docker + Docker Compose
- **CI/CD:** GitHub Actions

## Features

### User Service
- User registration and login with BCrypt password hashing
- JWT-based authentication (24h expiry)
- Role-based access control (USER, ADMIN)
- Full CRUD operations

### Product Service
- Product catalog with CRUD operations
- Category management
- Search by name
- Stock management
- Soft delete (active flag)

### Cart Service
- Add items to cart (with quantity merge)
- Update item quantities
- Remove items
- Clear cart
- Total calculation

### Order Service
- Order placement with item details
- Order status tracking (PENDING → SHIPPED → DELIVERED / CANCELLED)
- Order history
- Stock validation

### Payment Service
- Simulated payment processing (Stripe, PayPal, Credit/Debit Card)
- Transaction ID generation
- Payment status tracking
- Refund processing

### Notification Service
- Async notifications via RabbitMQ
- Email and SMS simulation
- Notification history per user
- Order event listeners

## Quick Start

### Prerequisites

- Docker & Docker Compose
- Java 21 (for local development)
- Maven (for local development)

### Running with Docker

```bash
# Build and start all services
docker compose up -d

# Check status
docker compose ps

# View logs
docker compose logs -f

# Stop all services
docker compose down
```

### Running Locally

```bash
# Start infrastructure
docker compose up -d postgres-users postgres-products postgres-carts postgres-orders postgres-payments postgres-notifications rabbitmq

# Start Eureka first
cd eureka-server && mvn spring-boot:run

# Then start each service (in separate terminals)
cd user-service && mvn spring-boot:run
cd product-service && mvn spring-boot:run
cd cart-service && mvn spring-boot:run
cd order-service && mvn spring-boot:run
cd payment-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
```

## API Endpoints

All requests go through the API Gateway at `http://localhost:8080`.

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users/register` | Register new user |
| POST | `/api/users/login` | Login (returns JWT) |

### Users
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users/username/{username}` | Get user by username |
| GET | `/api/users` | List all users (admin) |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

### Products
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | List all products |
| GET | `/api/products/{id}` | Get product by ID |
| GET | `/api/products/search?name=` | Search products |
| GET | `/api/products/category/{id}` | Products by category |
| POST | `/api/products` | Create product |
| PUT | `/api/products/{id}` | Update product |
| DELETE | `/api/products/{id}` | Delete product |
| PUT | `/api/products/{id}/stock?quantity=` | Update stock |

### Categories
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products/categories` | List categories |
| GET | `/api/products/categories/{id}` | Get category |
| POST | `/api/products/categories` | Create category |
| PUT | `/api/products/categories/{id}` | Update category |
| DELETE | `/api/products/categories/{id}` | Delete category |

### Cart
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/cart/{userId}` | Get user's cart |
| POST | `/api/cart/{userId}/items` | Add item to cart |
| PUT | `/api/cart/{userId}/items/{productId}?quantity=` | Update item quantity |
| DELETE | `/api/cart/{userId}/items/{productId}` | Remove item |
| DELETE | `/api/cart/{userId}` | Clear cart |

### Orders
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders` | Create order |
| GET | `/api/orders/{id}` | Get order by ID |
| GET | `/api/orders/user/{userId}` | User's orders |
| GET | `/api/orders/status/{status}` | Orders by status |
| PUT | `/api/orders/{id}/status?status=` | Update status |
| PUT | `/api/orders/{id}/cancel` | Cancel order |

### Payments
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/payments` | Process payment |
| GET | `/api/payments/{id}` | Get payment by ID |
| GET | `/api/payments/order/{orderId}` | Payment for order |
| GET | `/api/payments/user/{userId}` | User's payments |
| PUT | `/api/payments/{id}/refund` | Refund payment |

### Notifications
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/notifications` | Send notification |
| GET | `/api/notifications/user/{userId}` | User's notifications |

## Example Usage

```bash
# Register
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"pass123","fullName":"John Doe"}'

# Login (get JWT token)
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"pass123"}'

# Browse products (with JWT)
curl http://localhost:8080/api/products \
  -H "Authorization: Bearer <token>"

# Add to cart
curl -X POST http://localhost:8080/api/cart/1/items \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"productId":1,"quantity":2}'

# Place order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"userId":1,"items":[{"productId":1,"quantity":2}],"shippingAddress":"123 Main St"}'
```

## Monitoring

- **Eureka Dashboard:** http://localhost:8761
- **RabbitMQ Management:** http://localhost:15672 (guest/guest)
- **Service Health:** http://localhost:8081/actuator/health (replace port per service)

## License

MIT
