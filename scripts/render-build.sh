#!/usr/bin/env bash
# Render "Node" ortamında JDK yok; bu script Temurin 17 indirip ./mvnw ile jar üretir.
# Build Command: bash scripts/render-build.sh
#
# JDK proje köküne (.render-jdk) çıkarılır; /tmp deploy aşamasında yok olur, runtime java bulamazdı.
# Start Command: bash scripts/render-start.sh
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

JDK_DIR="$ROOT_DIR/.render-jdk"
rm -rf "$JDK_DIR"
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
