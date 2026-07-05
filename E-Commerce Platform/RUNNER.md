# RUNNER.md — How to Run the E-Commerce Platform

Step-by-step guide to get the full microservices stack running locally.

---

## Prerequisites

| Tool | Version | Check |
|------|---------|-------|
| **Docker** | 24+ | `docker --version` |
| **Docker Compose** | v2+ | `docker compose version` |
| **Git** | any | `git --version` |

> **macOS users:** If you don't have Docker Desktop, install [Colima](https://brew.sh) as a lightweight alternative:
> ```bash
> brew install colima docker
> colima start
> ```

---

## 1. Clone the Repository

```bash
git clone https://github.com/srsudip/Roadmap-Projects.git
cd "Roadmap-Projects/E-Commerce Platform"
```

---

## 2. Start Everything (Development)

```bash
docker compose up -d --build
```

This builds all 9 service images and starts **16 containers**:

| Category | Containers | Ports |
|----------|-----------|-------|
| PostgreSQL | 6 databases | 5433–5438 |
| RabbitMQ | 1 | 5672, 15672 |
| Eureka Server | 1 | 8761 |
| API Gateway | 1 | 8080 |
| Backend Services | 6 | 8081–8086 |
| Frontend | 1 | 3000 |

**First build takes ~3–5 minutes.** Subsequent builds are fast (Maven cache + Docker layer cache).

---

## 3. Verify It's Running

Wait ~60 seconds for all services to boot, then:

```bash
# Check all containers are healthy
docker compose ps

# You should see all 16 containers with (healthy) status
```

Or check specific services:

```bash
# Eureka dashboard
curl -s http://localhost:8761/health

# API Gateway
curl -s http://localhost:8080/actuator/health

# Frontend
curl -s -o /dev/null -w "%{http_code}" http://localhost:3000
# Should return: 200
```

---

## 4. Access the Application

### Web UI

Open **http://localhost:3000** in your browser.

### Default Admin Account

| Field | Value |
|-------|-------|
| Username | `admin` |
| Password | `admin123` |

> Register a new user at http://localhost:3000/signup if you don't want to use admin.

### Service Dashboards

| URL | Description | Credentials |
|-----|-------------|-------------|
| http://localhost:3000 | ShopHub Web UI | admin/admin123 |
| http://localhost:8761 | Eureka Dashboard | — |
| http://localhost:15672 | RabbitMQ Management | guest/guest |
| http://localhost:8080 | API Gateway (direct) | — |

---

## 5. Test the API

### Register a user
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"pass123","fullName":"John Doe"}'
```

### Login and get JWT
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"pass123"}'
```

### Browse products (no auth needed)
```bash
curl http://localhost:8080/api/products | head -c 500
```

### Add to cart
```bash
curl -X POST http://localhost:8080/api/cart/1/items \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"productName":"Headphones","price":79.99,"quantity":2}'
```

### Create an order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"shippingAddress":"123 Main St","items":[{"productId":1,"productName":"Headphones","price":79.99,"quantity":2}]}'
```

---

## 6. Hot-Reload (Development)

The dev setup mounts source code into containers. Changes to Java/Thymeleaf files auto-reload:

- **Backend services:** Edit files in `backend/<service>/src/`
- **Frontend:** Edit files in `frontend/frontend-service/src/`

After editing, the service rebuilds automatically (~10–15 seconds).

> **Note:** If hot-reload doesn't trigger, restart the specific service:
> ```bash
> docker compose restart user-service
> ```

---

## 7. Production Setup

For a production-like deployment without volume mounts:

```bash
# Build production images
docker compose -f docker-compose-prod.yml build

# Or pull from registry (if configured)
docker compose -f docker-compose-prod.yml pull

# Start
docker compose -f docker-compose-prod.yml up -d
```

Production differences:
- No volume mounts (no hot-reload)
- Non-root user in containers
- Pre-built images from GHCR
- Same health checks and dependency chains

---

## 8. Useful Commands

### Container management
```bash
# View running containers
docker compose ps

# View logs for a specific service
docker compose logs -f user-service

# View logs for all services
docker compose logs -f

# Restart a single service
docker compose restart product-service

# Stop everything
docker compose down

# Stop and remove all data (databases, volumes)
docker compose down -v
```

### Rebuild
```bash
# Rebuild a single service
docker compose build user-service
docker compose up -d user-service

# Rebuild everything
docker compose up -d --build
```

### Database access
```bash
# Connect to users database
docker compose exec postgres-users psql -U postgres -d userdb

# Connect to products database
docker compose exec postgres-products psql -U postgres -d productdb

# List all tables (inside psql)
\dt

# Quit psql
\q
```

### Cleanup
```bash
# Remove stopped containers
docker compose down

# Remove everything: containers, images, volumes, networks
docker compose down --rmi all --volumes --remove-orphans

# Clean up orphan Docker resources system-wide
docker image prune -af
docker volume prune -f
```

---

## 9. Troubleshooting

### Containers won't start

**Check logs:**
```bash
docker compose logs <service-name>
```

**Common issues:**
- **Port conflict:** Another process using port 8080/3000/5433? Stop it or change ports in `docker-compose.yml`
- **Docker not running:** `docker info` should return system info
- **Out of memory:** Docker Desktop needs at least 4GB RAM allocated

### Services not healthy

**Check health status:**
```bash
docker compose ps
# Look for (healthy) vs (unhealthy) vs (starting)
```

**Wait longer:** Some services take 30–60 seconds to become healthy on first boot.

**Check health manually:**
```bash
docker compose exec user-service wget -q --spider http://localhost:8081/actuator/health
```

### Database connection errors

**Ensure databases are ready:**
```bash
docker compose logs postgres-users | grep "ready to accept connections"
```

**Restart the dependent service:**
```bash
docker compose restart user-service
```

### Frontend shows errors

**Check API Gateway is running:**
```bash
curl http://localhost:8080/actuator/health
```

**Check frontend can reach gateway:**
```bash
docker compose logs frontend | grep -i "error\|exception"
```

### Build failures

**Clean build cache:**
```bash
docker compose down --rmi all --volumes
docker builder prune -af
docker compose up -d --build
```

**Check Dockerfile syntax:**
```bash
docker build --build-arg SERVICE=eureka-server --target development .
```

---

## 10. Architecture Quick Reference

```
User → Browser → Frontend (:3000) → API Gateway (:8080) → Microservices
                                                                  ↓
                                                         PostgreSQL (per service)
                                                         RabbitMQ (notifications)
                                                         Eureka (discovery)
```

| Component | Purpose |
|-----------|---------|
| **Eureka Server** | Service discovery — services register themselves here |
| **API Gateway** | Single entry point — routes requests to correct service |
| **Frontend** | Thymeleaf web UI — renders HTML server-side |
| **User Service** | JWT auth, user CRUD |
| **Product Service** | Product catalog, search, categories |
| **Cart Service** | Per-user shopping cart |
| **Order Service** | Order lifecycle management |
| **Payment Service** | Simulated payment processing |
| **Notification Service** | Async email/SMS via RabbitMQ |
