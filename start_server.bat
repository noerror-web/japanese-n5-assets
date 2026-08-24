@echo off
title Japanese Study App N5 - Asset Server
echo =========================================================
echo  Japanese Study App N5 - Local Asset Server
echo =========================================================
echo  Serving 92 Textbook PDFs & MP3 Audio Tracks
echo.
echo  PC Local IP: http://192.168.10.14:8000/
echo  Emulator IP: http://10.0.2.2:8000/
echo =========================================================
echo.
"C:\Users\Administrator\.gradle\jdks\eclipse_adoptium-17-amd64-windows.2\bin\java.exe" "C:\Users\Administrator\Music\online_assets\SimpleHttpServer.java"
pause
