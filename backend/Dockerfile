
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
COPY modules/common/pom.xml modules/common/pom.xml
COPY modules/domain/pom.xml modules/domain/pom.xml
COPY modules/application/pom.xml modules/application/pom.xml
COPY modules/infrastructure/pom.xml modules/infrastructure/pom.xml
COPY modules/api/pom.xml modules/api/pom.xml
COPY app/pom.xml app/pom.xml
RUN mvn -q -B dependency:go-offline || true

COPY modules modules
COPY app app
RUN mvn -q -B -DskipTests clean package

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S officemind && adduser -S officemind -G officemind
WORKDIR /app
COPY --from=build /workspace/app/target/officemind-ai.jar app.jar
USER officemind

EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
