FROM gradle:8.0-jdk19 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM amazoncorretto:19-alpine3.16
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/kabot_api.jar
ENTRYPOINT ["java","-jar","/app/kabot_api.jar"]