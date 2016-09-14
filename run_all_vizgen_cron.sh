#!/bin/bash

echo ">>> init"

while true; do
  echo ">>> running sbt submit-All"
  cat /dev/null | sbt submit-All
  echo ">>> sleeping 1h"
  sleep 3600 # 1h
done
