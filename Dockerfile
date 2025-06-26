# --- Этап 1: Сборка приложения с помощью Maven ---
# Используем официальный образ Maven с JDK 21 для сборки
FROM maven:3.9-eclipse-temurin-21 AS builder

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем pom.xml для кэширования зависимостей
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем остальной исходный код
COPY src ./src

# Собираем приложение в JAR-файл, пропуская тесты
# При сборке будет использован dev-профиль по умолчанию, но это не страшно, т.к. JAR универсален
RUN mvn clean package -DskipTests


# --- Этап 2: Создание легковесного образа для запуска ---
# Используем официальный образ JRE 21
FROM eclipse-temurin:21-jre-jammy

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем JAR-файл, собранный на первом этапе
COPY --from=builder /app/target/*.jar app.jar

# Порт, на котором приложение будет работать внутри контейнера
EXPOSE 8080

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]