@echo off
setlocal
if not defined PIPE_PYTHON set "PIPE_PYTHON=python"
"%PIPE_PYTHON%" "%~dp0scripts\open_web.py"
if errorlevel 1 pause
