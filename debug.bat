@echo off
echo Iniciando Spring Boot en modo DEBUG...
echo Puerto de debug: 5005
echo Puerto de aplicación: 8080
echo.
echo Para conectar el debugger:
echo 1. En VS Code: F5 -^> "Debug Spring Boot"
echo 2. En IntelliJ: Run -^> Edit Configurations -^> Remote JVM Debug
echo.

mvn spring-boot:run "-Dspring-boot.run.jvmArguments=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -Dspring.profiles.active=dev" 