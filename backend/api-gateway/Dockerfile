# Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY jwt-common jwt-common/
COPY api-gateway api-gateway/
# We also need other modules' poms if they are referenced in the parent pom as <modules>, but normally for a simple build we just need to make sure the reactor can build.
# Copying all to be safe and simple given the shared parent structure
COPY . .
RUN mvn clean package -pl api-gateway -am -DskipTests

# Run Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/api-gateway/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
