FROM openjdk:11
COPY ./target/*.jar gateway-service.jar
ENTRYPOINT ["java","-jar","gateway-service.jar"]