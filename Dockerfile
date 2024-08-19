FROM openjdk:21-jdk-slim
ADD target/order-management-api.jar order-management-api.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/order-management-api.jar"]