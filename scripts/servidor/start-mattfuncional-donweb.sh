#!/bin/bash
# Arranque no interactivo (perfil donweb, puerto 8080)
set -e
cd /root/mattfuncional
# En shell no interactivo, .bashrc a veces no exporta variables; cargar MATT_* explícitamente
if [ -f "$HOME/.bashrc" ]; then
  eval "$(grep -E '^export MATT_(DB_USER|DB_PASSWORD)=' "$HOME/.bashrc" || true)"
fi
[ -z "${MATT_DB_PASSWORD:-}" ] && [ -f ".matt_db_password" ] && export MATT_DB_PASSWORD=$(tr -d '\n\r' < .matt_db_password)
JAR="target/mattfuncional-0.0.1-SNAPSHOT.jar"
mkdir -p logs
if pgrep -f "$JAR" >/dev/null; then
  echo "Ya hay un proceso con el JAR corriendo"
  exit 0
fi
nohup java -Xmx512m -Xms256m \
  -Dspring.datasource.username="${MATT_DB_USER:-mattfuncional_user}" \
  -Dspring.datasource.password="${MATT_DB_PASSWORD}" \
  -Dspring.profiles.active=donweb \
  -Dserver.port="${MATT_APP_PORT:-8080}" \
  -Dserver.address=0.0.0.0 \
  -jar "$JAR" >> logs/mattfuncional.log 2>&1 &
echo $! > mattfuncional.pid
sleep 10
tail -40 logs/mattfuncional.log
