@echo off
echo ==========================================
echo Campus Reservation System - Startup Script
echo ==========================================
echo.

REM Check if Maven wrapper exists
if not exist mvnw.cmd (
    echo ERROR: Maven wrapper (mvnw.cmd) not found!
    echo Please run this script from the project root directory.
    pause
    exit /b 1
)

echo [1/2] Building the project with Maven...
call mvnw.cmd clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo.
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo [2/2] Starting the Spring Boot application...
echo The application will be available at: http://localhost:8080
echo Press Ctrl+C to stop the server.
echo.

call mvnw.cmd spring-boot:run

pause