FROM amazoncorretto:11.0.15-alpine3.15
EXPOSE 8080
COPY ./backend/build/libs/gitactionboard.jar /app/
WORKDIR /app

CMD ["java", "-jar", "gitactionboard.jar"]
