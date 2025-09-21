@echo off
setlocal

echo ========================================
echo   Java Swing Login System
echo ========================================

echo.
echo [0] Checking Java...
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java executable not found in PATH. Please install JDK and add java to PATH.
    pause
    exit /b 1
)

rem --- JVM options ---
rem Force JVM timezone to a Postgres-accepted IANA ID to avoid "invalid value for parameter TimeZone" errors
set "JAVA_OPTS=-Duser.timezone=Asia/Ho_Chi_Minh"

echo.
echo [1] Compiling application...
if not exist "build\classes" mkdir build\classes

javac -cp "postgresql-42.7.6.jar" -d build\classes ^
    src\app\swing\*.java ^
    src\app\swing\configuration\*.java ^
    src\app\swing\contants\*.java ^
    src\app\swing\model\*.java ^
    src\app\swing\service\*.java ^
    src\app\swing\util\*.java ^
    src\app\swing\view\*.java ^
    src\app\swing\view\pages\*.java

if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)

echo [SUCCESS] Compilation completed!
echo.

echo [2] Running application...
set "CLASSPATH=build\classes;postgresql-42.7.6.jar"
java %JAVA_OPTS% -cp "%CLASSPATH%" app.swing.AppSwing

echo.
echo Application closed.
pause
endlocal
