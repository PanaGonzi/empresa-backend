# syntax=docker/dockerfile:1

# --- Etapa 1: compilar ---
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
# Descarga de dependencias en una capa propia (se cachea mientras no cambie el pom)
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -q dependency:go-offline
COPY src src
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -q -DskipTests package \
    && cp target/*.jar app.jar

# --- Etapa 2: imagen final, solo JRE y sin root ---
FROM eclipse-temurin:17-jre
RUN useradd --system --uid 1001 appuser
WORKDIR /app
COPY --from=build /app/app.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
