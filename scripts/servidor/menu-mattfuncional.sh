#!/bin/bash
# =============================================================================
# MATTFUNCIONAL - MENÚ DE GESTIÓN (Servidor Donweb)
# Uso: ./menu-mattfuncional.sh  (ejecutar desde el directorio del proyecto)
# =============================================================================

APP_NAME="Mattfuncional"
APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JAR_NAME="mattfuncional-0.0.1-SNAPSHOT.jar"
JAR_PATH="$APP_DIR/target/$JAR_NAME"
PID_FILE="$APP_DIR/mattfuncional.pid"
LOG_FILE="$APP_DIR/logs/mattfuncional.log"
APP_PORT="${MATT_APP_PORT:-8081}"
SPRING_PROFILE="donweb"

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

get_pid() {
    if [ -f "$PID_FILE" ]; then
        cat "$PID_FILE"
    else
        pgrep -f "mattfuncional.*$JAR_NAME" 2>/dev/null || echo ""
    fi
}

is_running() {
    local pid=$(get_pid)
    [ -n "$pid" ] && kill -0 "$pid" 2>/dev/null
}

show_status() {
    if is_running; then
        local pid=$(get_pid)
        echo -e "${GREEN}◆ $APP_NAME corriendo (PID: $pid) - Puerto $APP_PORT${NC}"
    else
        echo -e "${YELLOW}◆ $APP_NAME no está corriendo${NC}"
    fi
}

stop_app() {
    echo "Parando $APP_NAME..."
    if is_running; then
        local pid=$(get_pid)
        kill "$pid" 2>/dev/null
        for i in {1..15}; do
            if ! kill -0 "$pid" 2>/dev/null; then
                echo -e "${GREEN}Aplicación detenida.${NC}"
                rm -f "$PID_FILE"
                return 0
            fi
            sleep 1
        done
        kill -9 "$pid" 2>/dev/null
        rm -f "$PID_FILE"
        echo -e "${GREEN}Detenida (forzado).${NC}"
    else
        echo -e "${YELLOW}No estaba corriendo.${NC}"
    fi
}

update_code() {
    echo "Actualizando código (git pull)..."
    cd "$APP_DIR" || exit 1
    git pull
    echo -e "${GREEN}Listo.${NC}"
}

compile_app() {
    echo "Compilando $APP_NAME..."
    cd "$APP_DIR" || exit 1
    if [ -x "./mvnw" ]; then
        ./mvnw clean package -DskipTests -q
    else
        mvn clean package -DskipTests -q
    fi
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}Compilación correcta.${NC}"
    else
        echo -e "${RED}Error en compilación.${NC}"
        return 1
    fi
}

start_app() {
    if is_running; then
        echo -e "${YELLOW}$APP_NAME ya está corriendo.${NC}"
        return 0
    fi
    if [ ! -f "$JAR_PATH" ]; then
        echo -e "${RED}No se encuentra el JAR. Ejecuta opción 3 (Compilar) antes.${NC}"
        return 1
    fi
    mkdir -p "$(dirname "$LOG_FILE")"
    echo "Iniciando $APP_NAME en puerto $APP_PORT..."
    cd "$APP_DIR" || exit 1
    nohup java -Xmx512m -Xms256m \
        -Dspring.profiles.active=$SPRING_PROFILE \
        -Dserver.port=$APP_PORT \
        -jar "$JAR_PATH" \
        >> "$LOG_FILE" 2>&1 &
    echo $! > "$PID_FILE"
    sleep 2
    if is_running; then
        echo -e "${GREEN}$APP_NAME iniciada.${NC}"
    else
        echo -e "${RED}Error al iniciar. Revisa $LOG_FILE${NC}"
    fi
}

full_deploy() {
    echo "=== Despliegue completo (1+2+3+4) ==="
    stop_app
    update_code
    compile_app || return 1
    start_app
    echo -e "${GREEN}Despliegue completo finalizado.${NC}"
}

show_logs() {
    if [ -f "$LOG_FILE" ]; then
        tail -n 80 "$LOG_FILE"
    else
        echo "No hay archivo de log aún."
    fi
}

restart_app() {
    stop_app
    sleep 2
    start_app
}

show_info() {
    echo "Proyecto: $APP_NAME"
    echo "Directorio: $APP_DIR"
    echo "JAR: $JAR_PATH"
    echo "Perfil Spring: $SPRING_PROFILE"
    echo "Puerto: $APP_PORT"
    show_status
}

show_disk() {
    df -h
    echo ""
    du -sh "$APP_DIR" 2>/dev/null || true
}

# -----------------------------------------------------------------------------
# Menú
# -----------------------------------------------------------------------------
while true; do
    echo ""
    echo "=============================================="
    echo "  $APP_NAME - MENÚ DE GESTIÓN"
    echo "=============================================="
    show_status
    echo "----------------------------------------------"
    echo "  1.  Parar aplicación $APP_NAME"
    echo "  2.  Actualizar código (git pull)"
    echo "  3.  Compilar aplicación $APP_NAME"
    echo "  4.  Iniciar aplicación $APP_NAME"
    echo "  5.  Despliegue completo (1+2+3+4)"
    echo "  6.  Ver estado del sistema"
    echo "  7.  Ver logs de la aplicación"
    echo "  8.  Reiniciar aplicación $APP_NAME"
    echo "  9.  Información del proyecto"
    echo "  10. Ver espacio en disco"
    echo "  11. Salir"
    echo "----------------------------------------------"
    read -p "Ingresa tu opción (1-11): " opcion

    case $opcion in
        1) stop_app ;;
        2) update_code ;;
        3) compile_app ;;
        4) start_app ;;
        5) full_deploy ;;
        6) echo ""; echo "=== Estado del sistema ==="; free -h; echo ""; uptime ;;
        7) show_logs ;;
        8) restart_app ;;
        9) show_info ;;
        10) show_disk ;;
        11) echo "Hasta luego."; exit 0 ;;
        *) echo "Opción no válida." ;;
    esac
done
