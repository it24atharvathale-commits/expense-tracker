FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/expense-calculator-1.0.0.jar app.jar
ENV PORT=8080
EXPOSE 8080
USER 10001
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
