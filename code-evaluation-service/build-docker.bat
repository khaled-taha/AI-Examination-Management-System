@echo off
echo 🔨 Building Code Evaluation Service Docker Container
echo ====================================================

REM Check if Docker is running
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker is not running. Please start Docker and try again.
    pause
    exit /b 1
)
echo [SUCCESS] Docker is running

REM Clean up previous builds
echo [INFO] Cleaning up previous builds...
docker-compose down -v 2>nul
docker system prune -f 2>nul

REM Build the container
echo [INFO] Building Docker container...
docker-compose build --no-cache
if %errorlevel% neq 0 (
    echo [ERROR] Container build failed
    pause
    exit /b 1
)
echo [SUCCESS] Container built successfully

REM Start the services
echo [INFO] Starting services...
docker-compose up -d
if %errorlevel% neq 0 (
    echo [ERROR] Failed to start services
    pause
    exit /b 1
)
echo [SUCCESS] Services started successfully

REM Wait for services to be ready
echo [INFO] Waiting for services to be ready...
set /a attempts=0
:wait_loop
set /a attempts+=1
if %attempts% gtr 60 (
    echo [ERROR] Services failed to start within expected time
    echo [INFO] Checking container logs...
    docker-compose logs --tail=20
    pause
    exit /b 1
)

curl -s http://localhost:8080/actuator/health >nul 2>&1
if %errorlevel% neq 0 (
    echo [INFO] Attempt %attempts%/60 - Services not ready yet...
    timeout /t 5 /nobreak >nul
    goto wait_loop
)

echo [SUCCESS] Code evaluation service is ready!

REM Run quick tests
echo [INFO] Running quick tests...

REM Test health endpoint
curl -s http://localhost:8080/actuator/health | findstr "UP" >nul
if %errorlevel% equ 0 (
    echo [SUCCESS] Health check passed
) else (
    echo [ERROR] Health check failed
    goto :error
)

REM Test languages endpoint
curl -s http://localhost:8080/api/v1/code-evaluation/languages | findstr "java" >nul
if %errorlevel% equ 0 (
    echo [SUCCESS] Languages endpoint working
) else (
    echo [ERROR] Languages endpoint failed
    goto :error
)

REM Test a simple Java evaluation
curl -s -X POST -H "Content-Type: application/json" -d "{\"code\":\"public class Solution { public static void main(String[] args) { System.out.println(\"Hello World\"); } }\",\"language\":\"java\",\"testCases\":[{\"input\":\"\",\"expectedOutput\":\"Hello World\",\"mark\":1.0,\"isSample\":false}]}" http://localhost:8080/api/v1/code-evaluation/evaluate | findstr "success" | findstr "true" >nul
if %errorlevel% equ 0 (
    echo [SUCCESS] Java evaluation test passed
) else (
    echo [ERROR] Java evaluation test failed
    goto :error
)

goto :success

:error
echo [ERROR] Tests failed
pause
exit /b 1

:success
echo.
echo 🎉 Code Evaluation Service is ready!
echo.
echo 📋 Service Information:
echo   • Service URL: http://localhost:8080
echo   • Health Check: http://localhost:8080/actuator/health
echo   • API Documentation: http://localhost:8080/api/v1/code-evaluation
echo.
echo 🧪 Testing:
echo   • Run full test suite: test-docker.bat
echo   • Stop services: docker-compose down
echo   • View logs: docker-compose logs -f
echo.
echo 🔧 Container Management:
echo   • View running containers: docker-compose ps
echo   • Restart services: docker-compose restart
echo   • Rebuild and restart: docker-compose up -d --build
echo.
pause 