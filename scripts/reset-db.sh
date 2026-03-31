#!/bin/sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname "$0")" && pwd)
PROJECT_DIR=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)

mkdir -p "$PROJECT_DIR/data/runtime"
cp "$PROJECT_DIR/data/seed/CSCI7785_database.db" "$PROJECT_DIR/data/runtime/CSCI7785_database.db"
