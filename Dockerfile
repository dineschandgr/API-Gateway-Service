FROM openjdk:11
WORKDIR /app
COPY ./target/gateway-service-0.0.1-SNAPSHOT.jar /app
EXPOSE 8769
CMD ["java", "-jar", "gateway-service-0.0.1-SNAPSHOT.jar"]
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=container","-jar","gateway-service-0.0.1-SNAPSHOT.jar"]