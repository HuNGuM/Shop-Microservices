# Используем базовый образ с OpenJDK
FROM openjdk:21-jdk-slim

# Указываем рабочую директорию в контейнере
WORKDIR /app

# Копируем jar-файл из целевой папки в рабочую директорию контейнера
COPY target/auth-service-1.0-SNAPSHOT-exec.jar auth-service.jar


# Открываем порт 8081 для Auth Service
EXPOSE 8081

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "/app/auth-service.jar"]




