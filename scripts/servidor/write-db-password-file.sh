#!/bin/bash
grep '^export MATT_DB_PASSWORD=' "$HOME/.bashrc" | cut -d= -f2- | tr -d "'" > /root/mattfuncional/.matt_db_password
chmod 600 /root/mattfuncional/.matt_db_password
echo OK
