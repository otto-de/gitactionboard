FROM amazoncorretto:17-alpine
EXPOSE 8080
COPY ./backend/build/libs/gitactionboard.jar /app/
WORKDIR /app

CMD ["java", "-jar", "gitactionboard.jar"]
