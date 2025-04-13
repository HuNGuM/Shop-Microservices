# Используем базовый образ с OpenJDK
FROM openjdk:21-jdk-slim

# Указываем рабочую директорию в контейнере
WORKDIR /app

# Копируем jar-файл из целевой папки в рабочую директорию контейнера
COPY target/user-service-1.0-SNAPSHOT-exec.jar user-service.jar

# Открываем порт 8084 для User Service
EXPOSE 8084

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]
