FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ARG EMAIL_PASSWORD
ENTRYPOINT ["java","-jar", "--enable-preview", "-Dspring.profiles.active=prod", "/app.jar"]
EXPOSE 8443
