FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline 
COPY src ./src
RUN if [ ! -f src/main/resources/app.key ]; then \
      openssl genpkey -algorithm RSA -out src/main/resources/app.key -pkeyopt rsa_keygen_bits:2048 && \
      openssl rsa -pubout -in src/main/resources/app.key -out src/main/resources/app.pub; \
    fi

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx350m", "-Xms256m", "-Xss512k", "-XX:+UseSerialGC", "-Dserver.port=${PORT:8080}", "-jar", "app.jar"]
