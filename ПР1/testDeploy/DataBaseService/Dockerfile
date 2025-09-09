FROM amazoncorretto:22 AS builder

WORKDIR /application
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
