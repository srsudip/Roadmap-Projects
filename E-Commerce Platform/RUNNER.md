# RUNNER.md — E-Commerce Platform

## Build & Start

```bash
docker compose up -d --build
```

Waits ~60s for all 16 containers to become healthy. Verify with:

```bash
docker compose ps
```

## Clean Up

```bash
# Stop containers
docker compose down

# Full cleanup: containers, images, volumes, networks
docker compose down --rmi all --volumes --remove-orphans

# Clean orphan Docker resources system-wide
docker image prune -af
docker volume prune -f
```

## URLs

| Service | URL |
|---------|-----|
| Frontend | http://localhost:3000 |
| API Gateway | http://localhost:8080 |
| Eureka Dashboard | http://localhost:8761 |
| RabbitMQ Management | http://localhost:15672 |

## Default Credentials

| Account | Username | Password |
|---------|----------|----------|
| Admin | `admin` | `admin123` |
| RabbitMQ | `guest` | `guest` |
