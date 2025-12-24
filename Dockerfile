FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /home/gradle/project

RUN apk add --no-cache bash unzip

ENV GRADLE_USER_HOME=/home/gradle/.gradle

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./

RUN chmod +x ./gradlew && ./gradlew --no-daemon dependencies || true

COPY . .
RUN chmod +x ./gradlew && ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -XX:+AlwaysActAsServerClassMachine"
WORKDIR /app
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar
EXPOSE 8080
USER 10001
ENTRYPOINT ["java","-jar","/app/app.jar"]
