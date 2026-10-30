@echo off
setlocal
cd /d "%~dp0..\harmony-app"
if not defined DEVECO_HOME if exist "local.properties" (
  for /f "usebackq tokens=1,* delims==" %%A in ("local.properties") do if "%%A"=="sdk.dir" set "DEVECO_HOME=%%B\.."
)
if not defined DEVECO_HOME (
  echo Set DEVECO_HOME to your DevEco Studio directory, or open this project in DevEco Studio first.
  pause
  exit /b 1
)
for %%I in ("%DEVECO_HOME%") do set "DEVECO_HOME=%%~fI"
if not exist "%DEVECO_HOME%\tools\hvigor\bin\hvigorw.bat" (
  echo Could not find hvigorw.bat under DEVECO_HOME.
  pause
  exit /b 1
)
set "DEVECO_SDK_HOME=%DEVECO_HOME%\sdk"
set "NODE_HOME=%DEVECO_HOME%\tools\node"
set "JAVA_HOME=%DEVECO_HOME%\jbr"
set "PATH=%NODE_HOME%;%JAVA_HOME%\bin;%PATH%"
call "%DEVECO_HOME%\tools\hvigor\bin\hvigorw.bat" --mode module -p product=default assembleHap --no-daemon
set "PIPE_BUILD_EXIT=%ERRORLEVEL%"
pause
exit /b %PIPE_BUILD_EXIT%
