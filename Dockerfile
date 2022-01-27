FROM openjdk:11
VOLUME /tmp
COPY ./target/*.jar eureka-zuul-service.jar 
ENTRYPOINT ["java","-jar","eureka-zuul-service.jar"]
EXPOSE 8762