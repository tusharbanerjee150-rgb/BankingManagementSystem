@echo off

if exist out (
    rmdir /s /q out
)

mkdir out

echo.
echo ======================================
echo   Banking Management System - Build
echo ======================================
echo.
echo Compiling source files...

javac -d out ^
src\com\banking\cli\Main.java ^
src\com\banking\model\*.java ^
src\com\banking\admin\*.java ^
src\com\banking\service\*.java ^
src\com\banking\validation\*.java ^
src\com\banking\exception\*.java

if %errorlevel% neq 0 (
    echo.
    echo ======================================
    echo          BUILD FAILED
    echo ======================================
    exit /b 1
)

echo.
echo ======================================
echo          BUILD SUCCESSFUL
echo ======================================