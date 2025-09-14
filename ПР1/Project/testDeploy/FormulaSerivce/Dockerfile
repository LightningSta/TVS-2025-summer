FROM amazoncorretto:22 AS builder


# Копируем файл JAR в контейнер
WORKDIR /application
COPY target/*.jar app.jar
# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
