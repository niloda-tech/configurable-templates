# Deployment Guide - COT Simple Editor

This guide provides step-by-step instructions for deploying the COT Simple Editor to production.

## Table of Contents

1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Building for Production](#building-for-production)
4. [Deployment Options](#deployment-options)
5. [Environment Configuration](#environment-configuration)
6. [Monitoring and Health Checks](#monitoring-and-health-checks)
7. [Troubleshooting](#troubleshooting)

## Overview

The COT Simple Editor consists of two components:
- **Backend**: Ktor-based REST API (JVM)
- **Frontend**: Kobweb-based web UI (JavaScript)

Both can be deployed together or separately depending on your infrastructure.

## Prerequisites

- JDK 17 or higher
- Gradle 8.x or higher (wrapper included)
- Kobweb CLI (for frontend export)
- 2GB RAM minimum (4GB recommended)
- Port 8080 available for backend

## Building for Production

### Backend Build

Build the backend JAR with all dependencies:

```bash
# From project root
./gradlew :cot-simple-endpoints:build

# The JAR will be created at:
# cot-simple-endpoints/build/libs/cot-simple-endpoints-1.0-SNAPSHOT.jar
```

### Frontend Build

Build the frontend for production:

```bash
# Install Kobweb CLI (one-time setup)
# macOS
brew install varabyte/tap/kobweb

# Linux/Windows - Download from https://github.com/varabyte/kobweb/releases

# Export frontend
cd cot-frontend
kobweb export --layout static

# Output will be in .kobweb/site/
```

## Deployment Options

### Option 1: Docker Deployment (Recommended)

Create a `Dockerfile` for the backend:

```dockerfile
FROM eclipse-temurin:17-jre-alpine

# Create app directory
WORKDIR /app

# Copy JAR file
COPY cot-simple-endpoints/build/libs/cot-simple-endpoints-1.0-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Create a `docker-compose.yml`:

```yaml
version: '3.8'

services:
  backend:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      - JAVA_OPTS=-Xmx512m -Xms256m
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8080/health"]
      interval: 30s
      timeout: 3s
      retries: 3
      start_period: 10s

  frontend:
    image: nginx:alpine
    ports:
      - "80:80"
    volumes:
      - ./cot-frontend/.kobweb/site:/usr/share/nginx/html:ro
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
    depends_on:
      - backend
    restart: unless-stopped
```

Create `nginx.conf` for frontend:

```nginx
events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    server {
        listen 80;
        server_name _;
        root /usr/share/nginx/html;
        index index.html;

        # Frontend static files
        location / {
            try_files $uri $uri/ /index.html;
        }

        # Proxy API requests to backend
        location /api/ {
            proxy_pass http://backend:8080;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # Health check endpoint
        location /health {
            proxy_pass http://backend:8080/health;
        }
    }
}
```

Deploy with Docker:

```bash
# Build applications
./gradlew :cot-simple-endpoints:build
cd cot-frontend && kobweb export --layout static && cd ..

# Start services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Option 2: Standalone JAR Deployment

Run backend as a standalone service:

```bash
# Build
./gradlew :cot-simple-endpoints:build

# Run
java -jar cot-simple-endpoints/build/libs/cot-simple-endpoints-1.0-SNAPSHOT.jar

# With custom JVM options
java -Xmx512m -Xms256m -jar cot-simple-endpoints/build/libs/cot-simple-endpoints-1.0-SNAPSHOT.jar
```

Create a systemd service (Linux):

```ini
# /etc/systemd/system/cot-editor.service
[Unit]
Description=COT Simple Editor Backend
After=network.target

[Service]
Type=simple
User=cot-editor
WorkingDirectory=/opt/cot-editor
ExecStart=/usr/bin/java -Xmx512m -Xms256m -jar /opt/cot-editor/cot-simple-endpoints.jar
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

Enable and start:

```bash
sudo systemctl daemon-reload
sudo systemctl enable cot-editor
sudo systemctl start cot-editor
sudo systemctl status cot-editor
```

### Option 3: Cloud Platform Deployment

#### Heroku

Create `Procfile`:

```
web: java -jar cot-simple-endpoints/build/libs/cot-simple-endpoints-1.0-SNAPSHOT.jar
```

Deploy:

```bash
heroku create your-app-name
git push heroku main
```

#### Google Cloud Run

```bash
# Build and push image
gcloud builds submit --tag gcr.io/PROJECT-ID/cot-editor

# Deploy
gcloud run deploy cot-editor \
  --image gcr.io/PROJECT-ID/cot-editor \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --port 8080
```

#### AWS Elastic Beanstalk

Create `.ebextensions/environment.config`:

```yaml
option_settings:
  aws:elasticbeanstalk:application:environment:
    PORT: 8080
  aws:elasticbeanstalk:container:java:
    JVMOptions: '-Xmx512m -Xms256m'
```

Deploy:

```bash
eb init -p java-17 cot-editor
eb create cot-editor-env
eb deploy
```

## Environment Configuration

### Backend Configuration

The backend can be configured via environment variables:

```bash
# Server port (default: 8080)
export SERVER_PORT=8080

# JVM memory settings
export JAVA_OPTS="-Xmx512m -Xms256m"
```

### Frontend Configuration

The frontend auto-detects the backend URL based on environment:
- **Development**: `http://localhost:8080`
- **Production**: Same origin as frontend

To override, modify `ApiClient.kt`:

```kotlin
private val apiBaseUrl = System.getenv("BACKEND_URL") ?: run {
    val origin = window.location.origin
    if (origin.contains(":8081")) {
        "http://localhost:8080"
    } else {
        origin
    }
}
```

## Monitoring and Health Checks

### Health Check Endpoint

The backend provides a health check endpoint:

```bash
curl http://localhost:8080/health
# Response: {"status":"ok"}
```

### Monitoring Recommendations

1. **Health Checks**: Monitor `/health` endpoint every 30 seconds
2. **Logs**: Collect logs from stdout/stderr
3. **Metrics**: Consider adding Micrometer for metrics
4. **Alerts**: Set up alerts for:
   - HTTP 5xx errors
   - Response time > 1s
   - Health check failures

### Example Prometheus Configuration

```yaml
scrape_configs:
  - job_name: 'cot-editor'
    metrics_path: '/metrics'
    static_configs:
      - targets: ['localhost:8080']
```

## Scaling Considerations

### Horizontal Scaling

The current implementation uses in-memory storage, which doesn't support horizontal scaling. To enable scaling:

1. **Replace In-Memory Repository**: Implement persistent storage (PostgreSQL, MongoDB)
2. **Session Management**: Use Redis for session storage
3. **Load Balancer**: Use nginx or cloud load balancer
4. **Sticky Sessions**: If keeping in-memory storage, use sticky sessions

### Vertical Scaling

Recommended resources per instance:
- **Small**: 2 CPU cores, 2GB RAM (< 100 concurrent users)
- **Medium**: 4 CPU cores, 4GB RAM (< 500 concurrent users)
- **Large**: 8 CPU cores, 8GB RAM (< 1000 concurrent users)

## Performance Optimization

### Backend Optimizations

```bash
# Enable JVM optimizations
java -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+UseStringDeduplication \
     -Xmx512m \
     -Xms512m \
     -jar app.jar
```

### Frontend Optimizations

1. **Enable Compression**: Configure nginx/CDN to compress static files
2. **Cache Static Assets**: Set cache headers for JS/CSS
3. **CDN**: Serve frontend from CDN for better global performance

Example nginx caching:

```nginx
location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}
```

## Security Best Practices

1. **HTTPS Only**: Always use HTTPS in production
2. **CORS Configuration**: Restrict CORS to specific origins
3. **Rate Limiting**: Implement rate limiting to prevent abuse
4. **Input Validation**: Validate all user inputs
5. **Security Headers**: Add security headers

Example security headers (nginx):

```nginx
add_header X-Frame-Options "SAMEORIGIN" always;
add_header X-Content-Type-Options "nosniff" always;
add_header X-XSS-Protection "1; mode=block" always;
add_header Referrer-Policy "no-referrer-when-downgrade" always;
add_header Content-Security-Policy "default-src 'self' http: https: data: blob: 'unsafe-inline'" always;
```

## Backup and Recovery

### Data Backup

Since the application uses in-memory storage by default:

1. **Export COTs**: Implement export functionality
2. **Periodic Backups**: If using persistent storage, schedule regular backups
3. **Disaster Recovery**: Document recovery procedures

### Database Migration (Future)

When migrating to persistent storage:

```sql
-- Example PostgreSQL schema
CREATE TABLE cots (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    dsl_code TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_cots_name ON cots(name);
CREATE INDEX idx_cots_created_at ON cots(created_at);
```

## Troubleshooting

### Common Issues

#### Port Already in Use

```bash
# Find process using port 8080
lsof -i :8080
# or
netstat -tulpn | grep 8080

# Kill process
kill -9 <PID>
```

#### Out of Memory

```bash
# Increase heap size
java -Xmx1g -Xms512m -jar app.jar
```

#### Frontend Can't Reach Backend

Check CORS configuration in `Application.kt`:

```kotlin
install(CORS) {
    allowMethod(HttpMethod.Options)
    allowMethod(HttpMethod.Get)
    allowMethod(HttpMethod.Post)
    allowMethod(HttpMethod.Put)
    allowMethod(HttpMethod.Delete)
    allowHeader(HttpHeaders.Authorization)
    allowHeader(HttpHeaders.ContentType)
    allowHost("yourdomain.com", schemes = listOf("https"))
}
```

### Logs and Debugging

```bash
# View Docker logs
docker-compose logs -f backend

# View systemd logs
journalctl -u cot-editor -f

# Enable debug logging
java -Dorg.slf4j.simpleLogger.defaultLogLevel=debug -jar app.jar
```

## Upgrade Process

1. **Backup**: Export all COTs before upgrade
2. **Test**: Test new version in staging environment
3. **Deploy**: Use blue-green or rolling deployment
4. **Verify**: Check health endpoint and test critical features
5. **Rollback**: Be prepared to rollback if issues arise

### Zero-Downtime Deployment

```bash
# Start new version on different port
java -jar new-version.jar --server.port=8081

# Test new version
curl http://localhost:8081/health

# Switch traffic (update load balancer/nginx)
# Stop old version after monitoring new version
```

## Support and Maintenance

- **Logs**: Monitor application logs daily
- **Updates**: Keep dependencies up to date
- **Security Patches**: Apply security patches promptly
- **Performance**: Monitor response times and resource usage
- **Backup**: Verify backups regularly

## Conclusion

This deployment guide covers the essential aspects of deploying the COT Simple Editor. For production use, consider:

1. Implementing persistent storage
2. Setting up proper monitoring and alerting
3. Implementing authentication and authorization
4. Regular security audits
5. Load testing before production deployment

For questions or issues, refer to the project documentation or open an issue on GitHub.
