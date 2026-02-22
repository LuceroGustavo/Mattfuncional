#!/bin/bash
# Iniciar sesión screen "mattfuncional" con el menú (si no existe).
# Uso: ./iniciar-menu.sh
# Luego: screen -r mattfuncional

SESSION="mattfuncional"
DIR="/root/mattfuncional"

if screen -list | grep -q "\.$SESSION\s"; then
    echo "La sesión '$SESSION' ya existe."
    echo "Para entrar al menú ejecutá: screen -r $SESSION"
    exit 0
fi

export MATT_DB_USER=mattfuncional_user
export MATT_DB_PASSWORD=Matt2026

screen -dmS "$SESSION" bash -c "cd $DIR && export MATT_DB_USER=mattfuncional_user MATT_DB_PASSWORD=Matt2026 && exec ./mattfuncional"

sleep 1
if screen -list | grep -q "\.$SESSION\s"; then
    echo "Sesión '$SESSION' creada. Para entrar: screen -r $SESSION"
else
    echo "Error al crear la sesión."
    exit 1
fi
