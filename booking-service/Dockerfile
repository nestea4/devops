# Dockerfile для booking-service
# Залежить від lab4, user-service та bus-service як compile-scope залежностей
# Тому спочатку встановлюємо всі shared залежності
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

ENV MAVEN_OPTS="-Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true"

# 1. lab4
COPY pom.xml ./lab4/pom.xml
COPY src ./lab4/src
RUN mvn -f lab4/pom.xml install -DskipTests -q

# 2.  user-service (compile залежність)
COPY user-service/pom.xml ./user-service/pom.xml
COPY user-service/src ./user-service/src
RUN mvn -f user-service/pom.xml install -DskipTests -q

# 3. bus-service (compile залежність)
COPY bus-service/pom.xml ./bus-service/pom.xml
COPY bus-service/src ./bus-service/src
RUN mvn -f bus-service/pom.xml install -DskipTests -q

# 4. booking-service
COPY booking-service/pom.xml ./booking-service/pom.xml
COPY booking-service/src ./booking-service/src
RUN mvn -f booking-service/pom.xml package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/booking-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]