# Dockerfile для user-service

FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

ENV MAVEN_OPTS="-Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true"

#  встановити lab4
COPY pom.xml ./lab4/pom.xml
COPY src ./lab4/src
RUN mvn -f lab4/pom.xml install -DskipTests -q

# зібрати user-service
COPY user-service/pom.xml ./user-service/pom.xml
COPY user-service/src ./user-service/src
RUN mvn -f user-service/pom.xml package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/user-service/target/*-exec.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]