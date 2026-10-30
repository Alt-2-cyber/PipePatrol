@echo off
setlocal
cd /d "%~dp0..\backend"
if not exist "target\pipepatrol-backend-0.1.0.jar" (
  echo Build the backend first: cd backend ^&^& mvn package
  pause
  exit /b 1
)
set "PIPE_JAVA=java"
if defined JAVA_HOME set "PIPE_JAVA=%JAVA_HOME%\bin\java.exe"
"%PIPE_JAVA%" -jar target\pipepatrol-backend-0.1.0.jar
pause
