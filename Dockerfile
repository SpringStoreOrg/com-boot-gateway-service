FROM openjdk:11
COPY ./target/*.jar eureka-zuul-service.jar 
ENTRYPOINT ["java","-jar","eureka-zuul-service.jar"]
EXPOSE 8762