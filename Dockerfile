FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar", "--enable-preview","/app.jar"]
EXPOSE 8080
