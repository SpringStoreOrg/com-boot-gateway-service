FROM eclipse-temurin:17-jdk-alpine
COPY ./target/*.jar gateway-service.jar
ENTRYPOINT ["java","-jar","gateway-service.jar"]