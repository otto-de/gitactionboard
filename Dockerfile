FROM node:16.13.1-alpine3.14 as frontend_builder
COPY --chown=node:node ./frontend /home/node/frontend
WORKDIR /home/node/frontend
RUN npm install
RUN npm run build

FROM gradle:7.2.0-jdk11-alpine as backend_builder
COPY --chown=gradle:gradle ./backend /home/gradle/backend
WORKDIR /home/gradle/backend
COPY --from=frontend_builder /home/node/frontend/dist src/main/resources/public
RUN gradle --no-daemon bootJar

FROM openjdk:11-jre-slim
EXPOSE 8080
COPY --from=backend_builder /home/gradle/backend/build/libs/gitactionboard-1.0.0.jar /app/
WORKDIR /app

CMD ["java", "-jar", "gitactionboard-1.0.0.jar"]
