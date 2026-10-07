FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY api/pom.xml pom.xml
COPY api/src src
RUN mvn -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/API-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
