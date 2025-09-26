# Running StayFinder Locally

These commands run the Spring Boot app in this repo from e:/stayfinder

Windows (CMD) - using the bundled Maven wrapper:
```
mvnw.cmd spring-boot:run
```

Windows (PowerShell) - using the bundled Maven wrapper:
```
.\mvnw spring-boot:run
```

macOS / Linux (bash):
```
./mvnw spring-boot:run
```

Build a standalone jar and run (any OS):
```
mvnw.cmd clean package
java -jar target/*.jar
```

Notes:
- The app serves on http://localhost:8080 by default (check [`src/main/resources/application.properties`](src/main/resources/application.properties:1))
- If you changed the port, use the configured port.
- To see logs in real time, run the spring-boot:run command; stop with Ctrl+C.
- If the app fails to start due to DB errors, ensure the database is running or update application.properties for an in-memory H2 or correct credentials.

Quick check:
```
curl http://localhost:8080/
```

Previewing UI:
- Open http://localhost:8080/search in your browser to view the search page.

Troubleshooting:
- If mvnw.cmd is not executable, run mvn spring-boot:run if you have Maven installed.
- On Windows you may need to run with administrative privileges to bind to low ports.

End.