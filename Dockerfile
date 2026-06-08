# Etap 1: builder (Kompilacja aplikacji)
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Kopiujemy POM i pobieramy zależności, co pozwala na optymalizację cache'u
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Kopiujemy kod źródłowy oraz pliki JSON
COPY src ./src
COPY *.json ./
RUN mvn -B package -DskipTests

# Etap 2: runtime (Uruchomienie aplikacji w lekkim środowisku)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Kopiujemy gotowy plik JAR oraz pliki JSON z etapu buildera
COPY --from=builder /app/target/*.jar app.jar
COPY --from=builder /app/*.json ./

# Deklarujemy port (używamy 8081, ponieważ na taki zmieniliśmy w poprzednich krokach)
EXPOSE 8081

# Główny proces kontenera
ENTRYPOINT ["java", "-jar", "app.jar"]