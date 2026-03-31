FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY src ./src

RUN mvn -q -DskipTests package

FROM maven:3.9-eclipse-temurin-17
WORKDIR /app

COPY --from=build /workspace/target/demo-1.0-SNAPSHOT.jar /app/app.jar
COPY data /app/data
COPY scripts/docker-entrypoint.sh /app/docker-entrypoint.sh

RUN chmod +x /app/docker-entrypoint.sh

ENV EMP_DB_PATH=/app/data/runtime/CSCI7785_database.db
ENV APP_MAIN_CLASS=com.example.EmpDBConsoleApp

ENTRYPOINT ["/app/docker-entrypoint.sh"]
