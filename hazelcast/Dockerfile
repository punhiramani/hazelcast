FROM eclipse-temurin:17
WORKDIR /app

# Copy the pre-built jar file
COPY ./target/*.jar /app/hazelcast-service.jar

# Set default environment variables
ENV EXTERNAL_CONFIG_LOCATION=/app/config
ENV HAZELCAST_LOCAL_DEV=false

EXPOSE 8080 5701

# Using java -jar with optional config location syntax (optional:file:/app/config/)
# This prevents crashes if /app/config/ is not mounted yet while allowing ConfigMaps when mounted.
ENTRYPOINT ["java", "-Duser.timezone=Asia/Kathmandu", "-jar", "/app/hazelcast-service.jar", "--spring.config.additional-location=optional:file:/app/config/"]
