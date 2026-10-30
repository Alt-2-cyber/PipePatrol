@echo off
setlocal
set "PIPE_BIND=0.0.0.0"
echo LAN mode: use this computer's LAN IPv4 in the Harmony app.
call "%~dp0start-backend.cmd"
