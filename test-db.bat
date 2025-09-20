@echo off
echo ========================================
echo   Testing Database Connection
echo ========================================

echo.
echo [1] Compiling test program...
javac -cp "postgresql-42.7.6.jar" database/TestConnection.java

if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)

echo [SUCCESS] Compilation completed!
echo.

echo [2] Running connection test...
java -cp "postgresql-42.7.6.jar;database" TestConnection

echo.
echo Cleaning up...
del database\TestConnection.class 2>nul

echo.
pause 