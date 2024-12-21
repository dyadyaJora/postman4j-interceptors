#!/bin/bash

check_reload() {
  for arg in "$@"; do
    if [ "$arg" == "--reload" ]; then
      return 0
    fi
  done
  return 1
}

PACKAGE_FOLDER=../java/dev/jora/postman4j/models
SCHEMA_JSON=collection.json

if check_reload "$@"; then
  echo "Loading Postman collection schema..."
  curl https://schema.postman.com/collection/json/v2.1.0/draft-07/collection.json -o $SCHEMA_JSON
  echo "Schema loaded."
fi

echo "Generating folder..."
mkdir -p $PACKAGE_FOLDER

echo "Generating Java classes..."
npx quicktype \
  --src $SCHEMA_JSON \
  --src-lang schema \
  --lang java \
  --array-type list  \
  --no-lombok-copy-annotations \
  --lombok \
  --top-level PostmanCollection \
  --package dev.jora.postman4j.models --out $PACKAGE_FOLDER/. \

echo "Generation finished."