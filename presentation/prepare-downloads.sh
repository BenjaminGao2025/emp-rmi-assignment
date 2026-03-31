#!/bin/sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname "$0")" && pwd)
PROJECT_ROOT=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)
DOWNLOAD_DIR="$SCRIPT_DIR/downloads"
ARCHIVE_NAME="emp-rmi-assignment.zip"
TMP_DIR=$(mktemp -d "${TMPDIR:-/tmp}/emp-rmi-presentation.XXXXXX")
STAGE_DIR="$TMP_DIR/emp-rmi-assignment"

cleanup() {
  rm -rf "$TMP_DIR"
}

trap cleanup EXIT INT TERM

mkdir -p "$DOWNLOAD_DIR" "$STAGE_DIR"
rm -f "$DOWNLOAD_DIR/$ARCHIVE_NAME" "$DOWNLOAD_DIR/README.md"

cp "$PROJECT_ROOT/README.md" "$DOWNLOAD_DIR/README.md"

rsync -a \
  --exclude '.git' \
  --exclude 'target' \
  --exclude 'presentation/downloads' \
  --exclude 'data/runtime/CSCI7785_database.db' \
  "$PROJECT_ROOT/" "$STAGE_DIR/"

(cd "$TMP_DIR" && zip -qr "$DOWNLOAD_DIR/$ARCHIVE_NAME" "emp-rmi-assignment")

printf 'Prepared %s and %s\n' "$DOWNLOAD_DIR/README.md" "$DOWNLOAD_DIR/$ARCHIVE_NAME"
