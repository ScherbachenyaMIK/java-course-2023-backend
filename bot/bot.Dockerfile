FROM openjdk:21-jdk-slim

COPY ./target/bot.jar /bot/bot.jar

WORKDIR /bot

EXPOSE 8090

ENTRYPOINT ["java","-jar","/bot/bot.jar"]
