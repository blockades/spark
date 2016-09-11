#!/bin/bash

echo ">>> init"

while true; do
  echo ">>> running sbt submit-LineAll"
  cat /dev/null | sbt submit-LineAll
  echo ">>> running sbt submit-MiscAll"
  cat /dev/null | sbt submit-MiscAll
  echo ">>> sleeping 1h"
  sleep 3600 # 1h
done
