FROM openjdk:17-jdk-slim
COPY target/metamusic-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","-Dspring.datasource.url=${SPRING_DATASOURCE_URL}", \
"-Dspring.datasource.username=${SPRING_DATASOURCE_USERNAME}", \
"-Dspring.datasource.password=${SPRING_DATASOURCE_PASSWORD}" ,"/app.jar"]