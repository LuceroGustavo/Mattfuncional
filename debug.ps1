Write-Host "Iniciando Spring Boot en modo DEBUG..." -ForegroundColor Green
Write-Host "Puerto de debug: 5005" -ForegroundColor Yellow
Write-Host "Puerto de aplicación: 8080" -ForegroundColor Yellow
Write-Host ""
Write-Host "Para conectar el debugger:" -ForegroundColor Cyan
Write-Host "1. En VS Code: F5 -> 'Debug Spring Boot'" -ForegroundColor White
Write-Host "2. En IntelliJ: Run -> Edit Configurations -> Remote JVM Debug" -ForegroundColor White
Write-Host ""

$jvmArgs = "-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -Dspring.profiles.active=dev"
mvn spring-boot:run "-Dspring-boot.run.jvmArguments=$jvmArgs" 