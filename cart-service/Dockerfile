# Используем базовый образ с OpenJDK
FROM openjdk:21-jdk-slim

# Указываем рабочую директорию в контейнере
WORKDIR /app

# Копируем jar-файл из целевой папки в рабочую директорию контейнера
COPY target/cart-service-1.0-SNAPSHOT-exec.jar cart-service.jar

# Открываем порт 8082 для Cart Service
EXPOSE 8082

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "/app/cart-service.jar"]
