@echo off
echo 🚀 Starting Code Evaluation Service Docker Test
echo ================================================

REM Build and start containers
echo [INFO] Starting Docker containers...
docker-compose down -v 2>nul
docker-compose up -d --build

REM Wait for service to be ready
echo [INFO] Waiting for service to be ready...
set /a attempts=0
:wait_loop
set /a attempts+=1
if %attempts% gtr 30 (
    echo [ERROR] Service failed to start within expected time
    exit /b 1
)

curl -s http://localhost:8080/actuator/health >nul 2>&1
if %errorlevel% neq 0 (
    echo [INFO] Attempt %attempts%/30 - Service not ready yet...
    timeout /t 2 /nobreak >nul
    goto wait_loop
)

echo [SUCCESS] Service is ready!

REM Test Java
echo [INFO] Testing Java: Hello World
curl -s -X POST -H "Content-Type: application/json" -d "{\"code\":\"public class Solution { public static void main(String[] args) { System.out.println(\"Hello World\"); } }\",\"language\":\"java\",\"testCases\":[{\"input\":\"\",\"expectedOutput\":\"Hello World\",\"mark\":1.0,\"isSample\":false}]}" http://localhost:8080/api/v1/code-evaluation/evaluate

echo.
echo [INFO] Testing Python: Simple Print
curl -s -X POST -H "Content-Type: application/json" -d "{\"code\":\"print(\\\"Hello World\\\")\",\"language\":\"python\",\"testCases\":[{\"input\":\"\",\"expectedOutput\":\"Hello World\",\"mark\":1.0,\"isSample\":false}]}" http://localhost:8080/api/v1/code-evaluation/evaluate

echo.
echo [INFO] Testing C: Hello World
curl -s -X POST -H "Content-Type: application/json" -d "{\"code\":\"#include ^<stdio.h^>\\nint main() { printf(\\\"Hello World\\\"); return 0; }\",\"language\":\"c\",\"testCases\":[{\"input\":\"\",\"expectedOutput\":\"Hello World\",\"mark\":1.0,\"isSample\":false}]}" http://localhost:8080/api/v1/code-evaluation/evaluate

echo.
echo [INFO] Testing C++: Hello World
curl -s -X POST -H "Content-Type: application/json" -d "{\"code\":\"#include ^<iostream^>\\nint main() { std::cout ^<^< \\\"Hello World\\\"; return 0; }\",\"language\":\"c++\",\"testCases\":[{\"input\":\"\",\"expectedOutput\":\"Hello World\",\"mark\":1.0,\"isSample\":false}]}" http://localhost:8080/api/v1/code-evaluation/evaluate

echo.
echo [INFO] Testing SQL with database
curl -s -X POST -H "Content-Type: application/json" -d "{\"code\":\"SELECT COUNT(*) as count FROM employees WHERE department = \\\"Engineering\\\";\",\"language\":\"sql\",\"testCases\":[{\"input\":\"\",\"expectedOutput\":\"count\\n3\",\"mark\":1.0,\"isSample\":false}]}" http://localhost:8080/api/v1/code-evaluation/evaluate

echo.
echo [INFO] Testing service endpoints...

REM Test health endpoint
curl -s http://localhost:8080/actuator/health

echo.
echo [INFO] Testing supported languages
curl -s http://localhost:8080/api/v1/code-evaluation/languages

echo.
echo [INFO] Testing evaluator status
curl -s http://localhost:8080/api/v1/code-evaluation/status

echo.
echo [SUCCESS] All tests completed!
echo [INFO] Service is running at http://localhost:8080
echo [INFO] Health check: http://localhost:8080/actuator/health
echo [INFO] API documentation: http://localhost:8080/api/v1/code-evaluation

pause 