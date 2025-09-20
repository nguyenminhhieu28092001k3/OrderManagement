@echo off
echo ========================================
echo   Starting PostgreSQL with Docker
echo ========================================

echo.
echo [1] Starting PostgreSQL container...
docker-compose up -d postgres

if %errorlevel% neq 0 (
    echo [ERROR] Failed to start PostgreSQL container!
    echo Make sure Docker is running and try again.
    pause
    exit /b 1
)

echo.
echo [2] Waiting for PostgreSQL to be ready...
timeout /t 5 /nobreak > nul

:check_db
docker-compose exec postgres pg_isready -U postgres -d app_swing_db > nul 2>&1
if %errorlevel% neq 0 (
    echo Waiting for database to be ready...
    timeout /t 2 /nobreak > nul
    goto check_db
)

echo.
echo [SUCCESS] PostgreSQL is ready!
echo.
echo Database Information:
echo - Host: localhost
echo - Port: 5432
echo - Database: app_swing_db
echo - Username: postgres
echo - Password: 123456
echo.
echo You can now run your Java application!
echo.
echo To stop the database, run: docker-compose down
echo To view logs: docker-compose logs postgres
echo.
pause 