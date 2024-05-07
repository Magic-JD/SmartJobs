FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar", "--enable-preview", "-Dspring.profiles.active=prod", "/app.jar"]
EXPOSE 8443
