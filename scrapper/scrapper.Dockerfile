FROM openjdk:21-jdk-slim

COPY ./target/scrapper.jar /scrapper/scrapper.jar

WORKDIR /scrapper

EXPOSE 8080

ENTRYPOINT ["java","-jar","/scrapper/scrapper.jar"]
