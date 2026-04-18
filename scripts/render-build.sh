#!/usr/bin/env bash
# Render "Node" ortamında JDK yok; bu script Temurin 17 indirip ./mvnw ile jar üretir.
# Build Command: bash scripts/render-build.sh
set -euo pipefail

JDK_DIR="${TMPDIR:-/tmp}/akilli-jdk17"
mkdir -p "$JDK_DIR"

echo "→ JDK 17 indiriliyor (Adoptium API)..."
curl -fsSL \
  "https://api.adoptium.net/v3/binary/latest/17/ga/linux/x64/jdk/hotspot/normal/eclipse?project=jdk" \
  -o /tmp/akilli-jdk17.tgz

echo "→ Açılıyor: $JDK_DIR"
tar -xzf /tmp/akilli-jdk17.tgz -C "$JDK_DIR" --strip-components=1

export JAVA_HOME="$JDK_DIR"
export PATH="$JAVA_HOME/bin:$PATH"

java -version
echo "→ Maven wrapper ile build..."
./mvnw clean package -DskipTests
