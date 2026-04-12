#!/bin/bash
set -eu
DBPASS="$(openssl rand -base64 18 | tr -dc 'A-Za-z0-9' | head -c 22)"
mysql -e "CREATE DATABASE IF NOT EXISTS mattfuncional CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -e "CREATE USER IF NOT EXISTS 'mattfuncional_user'@'localhost' IDENTIFIED BY '${DBPASS}';"
mysql -e "GRANT ALL PRIVILEGES ON mattfuncional.* TO 'mattfuncional_user'@'localhost'; FLUSH PRIVILEGES;"
grep -q 'MATT_DB_USER=' ~/.bashrc 2>/dev/null || echo 'export MATT_DB_USER=mattfuncional_user' >> ~/.bashrc
grep -q 'MATT_DB_PASSWORD=' ~/.bashrc 2>/dev/null || sed -i '/export MATT_DB_PASSWORD=/d' ~/.bashrc
echo "export MATT_DB_PASSWORD=${DBPASS}" >> ~/.bashrc
echo "OK mattfuncional DB + mattfuncional_user"
echo "MATT_DB_PASSWORD (guardar en lugar seguro, también en ~/.bashrc): ${DBPASS}"
