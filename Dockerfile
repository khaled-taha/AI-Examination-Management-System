FROM openjdk:17-jdk-alpine
WORKDIR /exam-app
COPY target/exam-0.0.1-SNAPSHOT.jar exam.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "exam.jar"]
