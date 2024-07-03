FROM openjdk:17
EXPOSE 8082
ADD target/crypto-trading-system.jar crypto-trading-system.jar
ENTRYPOINT ["java", "-jar", "/crypto-trading-system.jar"]