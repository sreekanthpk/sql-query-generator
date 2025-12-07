<!-- File: README.md -->
# sql-query-generator

Spring Cloud Maven project scaffold for a SQL query generator service.

Build:
- On Windows (with Maven wrapper if present): `.\mvnw.cmd clean package`
- Or with local Maven: `mvn clean package`

Run:
- `mvn spring-boot:run` or run the generated jar: `java -jar target\sql-query-generator-0.0.1-SNAPSHOT.jar`

Notes:
- Uses Spring Cloud Config and Eureka client. Configure a Config Server and Eureka Server or adjust `src/main/resources/application.yml`.
- Java 17 recommended.