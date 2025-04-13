# Используем базовый образ с OpenJDK
FROM openjdk:21-jdk-slim

# Указываем рабочую директорию в контейнере
WORKDIR /app

# Копируем jar-файл из целевой папки в рабочую директорию контейнера
COPY target/product-service-1.0-SNAPSHOT-exec.jar product-service.jar

# Открываем порт 8083 для Product Service
EXPOSE 8083

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "/app/product-service.jar"]
