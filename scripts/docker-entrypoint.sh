#!/bin/sh
set -eu

DB_PATH="${EMP_DB_PATH:-/app/data/runtime/CSCI7785_database.db}"
SEED_PATH="/app/data/seed/CSCI7785_database.db"
MAIN_CLASS="${APP_MAIN_CLASS:-com.example.EmpDBConsoleApp}"

if [ ! -f "$DB_PATH" ]; then
  mkdir -p "$(dirname "$DB_PATH")"
  cp "$SEED_PATH" "$DB_PATH"
fi

exec java -cp /app/app.jar "$MAIN_CLASS" "$@"
