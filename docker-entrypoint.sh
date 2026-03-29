#!/bin/bash
##########################################################################
# docker-entrypoint.sh
# Inicia el display virtual Xvfb y luego ejecuta el comando del contenedor
##########################################################################
set -e

# Iniciar Xvfb (display virtual) para Chrome/Firefox en modo no-headless
Xvfb :99 -screen 0 1280x720x24 &
export DISPLAY=:99

# Esperar a que el display esté listo
sleep 1

echo "======================================================"
echo " Entorno de pruebas listo"
echo " Java:    $(java -version 2>&1 | head -1)"
echo " Maven:   $(mvn -version 2>&1 | head -1)"
echo " Chrome:  $(google-chrome --version)"
echo " Firefox: $(firefox --version)"
echo " Driver:  $(geckodriver --version | head -1)"
echo " Browser: ${BROWSER}"
echo " Headless: ${HEADLESS}"
echo "======================================================"

# Ejecutar el comando pasado al contenedor (por defecto: mvn test)
exec "$@"
