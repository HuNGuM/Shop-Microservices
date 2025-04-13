# Используем базовый образ с OpenJDK
FROM openjdk:21-jdk-slim

# Указываем рабочую директорию в контейнере
WORKDIR /app

# Копируем jar-файл из целевой папки в рабочую директорию контейнера
COPY target/api-gateway-1.0-SNAPSHOT-exec.jar api-gateway.jar

# Открываем порт 8080 для доступа к API Gateway
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "/app/api-gateway.jar"]
