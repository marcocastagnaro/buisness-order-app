FROM gradle:jdk21 AS build
WORKDIR /app

COPY --chown=gradle:gradle . /app
RUN gradle clean bootJar --no-daemon

FROM openjdk:21-slim AS runtime
WORKDIR /app

COPY --from=build /app/build/libs/*.jar ./app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]