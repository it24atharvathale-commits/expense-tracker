# ExpenseFlow — Java Expense Calculator

A colorful, responsive expense calculator using Java 17 + Maven, Selenium + JUnit 5, Jenkins, and Docker.

## Features
- Add an expense name, amount, category, and date
- Automatically calculate total and average expenses
- Count transactions and show category breakdown
- Delete individual expenses or clear all expenses
- Responsive layout with gradients, colored cards, tags, and validation

**Demo limitation:** expense data is held in browser memory and resets on refresh. No database is used.

## Requirements
JDK 17, Maven 3.8+, Chrome/compatible browser for Selenium, Git, and Docker for container stages.

## Run locally
Build the project and run the Selenium tests:
```bash
mvn clean package
```
The Selenium tests start the application on an available local port automatically, so you do not need to open a second terminal for testing.

To run the application after building:
```bash
java -jar target/expense-calculator-1.0.0.jar
```
Open http://localhost:8080. Selenium Manager may download/manage the browser driver, so internet access and a compatible Chrome browser are needed.

## Docker
```bash
mvn clean package -DskipTests
docker build -t YOUR_DOCKERHUB_USERNAME/expense-calculator:latest .
docker run --rm -p 8080:8080 YOUR_DOCKERHUB_USERNAME/expense-calculator:latest
```

## Jenkins setup
1. Use a Linux Jenkins agent with JDK 17, Maven, Git, Docker CLI and daemon, curl, Chrome, and Chrome runtime libraries.
2. Create a Pipeline job configured as Pipeline script from SCM, pointing to this Git repository.
3. Add Docker Hub credentials as **Username with password**, ID `dockerhub-credentials`; use a Docker Hub access token as the password.
4. Replace `YOUR_DOCKERHUB_USERNAME` in `Jenkinsfile`.
5. Run Build Now and inspect stage results and the published JUnit report.

The pipeline builds the app, starts it for Selenium tests, then builds and pushes the Docker image only if earlier stages pass.
