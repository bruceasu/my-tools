@if "%DEBUG%" == "" @echo off
TITLE My Tools GUI

setlocal

set APP_HOME=%~dp0..
set VERSION=1.0.0
set SERVICE=my-tools
set JAR=%SERVICE%-%VERSION%.one-jar.jar

cd %APP_HOME%
start /b "My Tools GUI" javaw -jar %JAR%
endlocal