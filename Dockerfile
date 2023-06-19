FROM amazoncorretto:17-alpine AS corretto-deps

COPY ./backend/build/libs/gitactionboard.jar /app/

RUN unzip /app/gitactionboard.jar -d temp &&  \
    jdeps \
      --print-module-deps \
      --ignore-missing-deps \
      --recursive \
      --multi-release 17 \
      --class-path="./temp/BOOT-INF/lib/*" \
      --module-path="./temp/BOOT-INF/lib/*" \
      /app/gitactionboard.jar > /modules.txt

FROM amazoncorretto:17-alpine AS corretto-jdk

COPY --from=corretto-deps /modules.txt /modules.txt

# hadolint ignore=DL3018
RUN apk add --no-cache binutils && \
    jlink \
         --verbose \
         --add-modules "$(cat /modules.txt),jdk.crypto.ec,jdk.crypto.cryptoki,jdk.management" \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /jre

FROM alpine:3.18.2
ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

RUN apk upgrade libssl3 libcrypto3

COPY --from=corretto-jdk /jre $JAVA_HOME

EXPOSE 8080
COPY ./backend/build/libs/gitactionboard.jar /app/
WORKDIR /app

CMD ["java", "-jar", "gitactionboard.jar"]
