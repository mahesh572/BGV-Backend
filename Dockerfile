FROM eclipse-temurin:21-jre-jammy

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} bgv-service.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/bgv-service.jar"]