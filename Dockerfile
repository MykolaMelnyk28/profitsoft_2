FROM maven:3.9.6-amazoncorretto-21 AS builder

COPY . /app/.

WORKDIR /app

RUN mvn clean package -DskipTests

FROM amazoncorretto:21

COPY --from=builder /app/target/*.jar /app/app.jar

EXPOSE ${SERVER_PORT}

ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]