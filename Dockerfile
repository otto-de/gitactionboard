FROM gradle:7.2.0-jdk11 as builder
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle --no-daemon bootJar

FROM openjdk:11-jre-slim
EXPOSE 8080
COPY --from=builder /home/gradle/src/build/libs/gitactionboard-1.0.0.jar /app/
WORKDIR /app

CMD ["java", "-jar", "gitactionboard-1.0.0.jar"]
