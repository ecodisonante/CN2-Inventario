# Build stage
FROM maven:3.9.6-amazoncorretto-21 AS builder
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Build app
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Configuración de la aplicación
EXPOSE 8080
ENV API_WAREHOUSES_URL=https://cn2-inventario.azurewebsites.net/api/warehouses \
    API_PRODUCTS_URL=https://cn2-inventario.azurewebsites.net/api/products \
    CORS_ALLOWED_ORIGINS=*

ENTRYPOINT ["java", "-jar", "app.jar"]