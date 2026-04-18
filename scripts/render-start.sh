#!/usr/bin/env bash
# Render Node runtime: PATH'te java yok; build'in kurduğu .render-jdk kullan.
# Start Command: bash scripts/render-start.sh
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
export JAVA_HOME="$ROOT/.render-jdk"
export PATH="$JAVA_HOME/bin:$PATH"

if [[ ! -x "$JAVA_HOME/bin/java" ]]; then
  echo "Hata: $JAVA_HOME/bin/java yok. Önce build'de bash scripts/render-build.sh çalışmalı." >&2
  exit 1
fi

JAR=$(find "$ROOT/target" -maxdepth 1 -name '*.jar' ! -name '*.original' | head -1)
if [[ -z "$JAR" ]]; then
  echo "Hata: target/*.jar bulunamadı." >&2
  exit 1
fi

exec java -Dserver.port="${PORT:-8080}" -jar "$JAR"
