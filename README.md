# EMP RMI Assignment

This project upgrades the provided SQLite EMP console app into a Java RMI system with explicit transactions and Docker-based concurrency experiments.

## Requirements covered

- Run the original non-distributed EMP application
- Convert EMP operations into a Java RMI distributed service
- Enable manual transaction handling in SQLite
- Run concurrent read/write clients and observe SQLite behavior

## Project layout

- `src/main/java/com/example/EmpDBConsoleApp.java`: original local console app backed by the shared service layer
- `src/main/java/com/example/app/EmpApplicationService.java`: connection lifecycle and manual transaction handling
- `src/main/java/com/example/rmi/`: RMI interface and implementation
- `src/main/java/com/example/server/ServerMain.java`: RMI registry and remote object bootstrap
- `src/main/java/com/example/client/RmiClientMain.java`: scripted and interactive RMI client
- `data/seed/CSCI7785_database.db`: original database
- `scripts/reset-db.sh`: reset runtime database to the seed state

## Build and test

```bash
docker run --rm -v "$PWD":/app -w /app maven:3.9-eclipse-temurin-17 mvn test
docker compose build
```

## Step 1: Run the non-distributed app

```bash
./scripts/reset-db.sh
docker compose run --rm local-app
```

Suggested check:

- Choose `1` to list all employees
- Choose `2` and query `E1`

## Step 2: Run the RMI server and client

Start the server:

```bash
./scripts/reset-db.sh
docker compose up -d rmi-server
```

Use scripted client commands:

```bash
docker compose run --rm rmi-client list
docker compose run --rm rmi-client find E1
docker compose run --rm rmi-client add E9 "A. Chen" Programmer
docker compose run --rm rmi-client update E9 "A. Chen" "Syst. Anal."
docker compose run --rm rmi-client delete E9
```

Use interactive client mode:

```bash
docker compose run --rm rmi-client
```

Stop the server:

```bash
docker compose down
```

## Step 3: Manual transaction behavior

Write operations are wrapped in explicit transactions inside `EmpApplicationService`.

- Success path: `setAutoCommit(false)` then `commit()`
- Failure path: `rollback()` before the exception is returned to the caller

Invalid titles and duplicate employee numbers are expected to fail and leave the database unchanged.

## Step 4: Concurrent transaction experiments

Reset the database and start the server:

```bash
./scripts/reset-db.sh
docker compose up -d rmi-server
```

Run the built-in experiment clients:

```bash
docker compose --profile experiment up --abort-on-container-exit client-read client-insert client-update
docker compose --profile experiment up --abort-on-container-exit client-insert client-update client-delete
docker compose logs --timestamps client-read client-insert client-update client-delete
docker compose run --rm rmi-client list
```

Expected observations:

- concurrent reads usually succeed together
- concurrent writes may serialize
- SQLite may report lock contention under pressure; that is valid experimental evidence if the database remains consistent

## Valid TITLE values for inserts and updates

Use only these existing `PAY.TITLE` values:

- `Elect. Eng.`
- `Mech. Eng.`
- `Programmer`
- `Syst. Anal.`

## Optional Tailscale extension

On the `7840` host, expose RMI ports with:

```bash
docker compose -f docker-compose.yml -f docker-compose.tailscale.yml up -d rmi-server
```

Then a client outside Docker can connect to `100.101.245.68:1099`. If you want to use the same Dockerized client from another machine, set:

```bash
RMI_SERVER_HOST=100.101.245.68
RMI_SERVER_PORT=1099
```
