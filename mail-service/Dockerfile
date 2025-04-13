# Используем базовый образ с OpenJDK
FROM openjdk:21-jdk-slim
# Указываем рабочую директорию в контейнере
WORKDIR /app

# Копируем jar-файл из целевой папки в рабочую директорию контейнера
COPY target/mail-service-1.0-SNAPSHOT-exec.jar mail-service.jar

# Открываем порт 8085 для Mail Service (поменяйте порт если нужно)
EXPOSE 8085

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "/app/mail-service.jar"]
