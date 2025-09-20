@echo off
echo ========================================
echo   Java Swing Login System
echo ========================================

echo.
echo [1] Compiling application...
if not exist "build\classes" mkdir build\classes

javac -cp "postgresql-42.7.6.jar" -d build\classes src\app\swing\*.java src\app\swing\configuration\*.java src\app\swing\contants\*.java src\app\swing\model\*.java src\app\swing\service\*.java src\app\swing\util\*.java src\app\swing\view\*.java src\app\swing\view\pages\*.java

if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)

echo [SUCCESS] Compilation completed!
echo.

echo [2] Running application...
java -cp "build\classes;postgresql-42.7.6.jar" app.swing.AppSwing

echo.
echo Application closed.
pause 