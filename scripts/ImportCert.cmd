@if "%DEBUG%"=="" @echo off
chcp 65001

if "%1" == "" (
    echo Не был указан путь к файлу сертификата для импорта
    echo Используйте:
    echo ImportCert.cmd "путь_к_файлу_сертификата" [пароль]
    echo   пароль - по умолчанию "changeme" ^(опционально^)
    set ERRORLEVEL=160
    goto fail
)

if not exist %1 (
    echo Не удалось найти файл сертификата для импорта
    echo Используйте:
    echo ImportCert.cmd "путь_к_файлу_сертификата" [пароль]
    echo   пароль - по умолчанию "changeme" ^(опционально^)
    set ERRORLEVEL=160
    goto fail
)

set CERT_FILE=%1
if "%2" == "" (
    set PASSWORD=changeme
) else (
    set PASSWORD=%2
)

if defined JAVA_HOME goto findJavaFromJavaHome

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set KEYTOOL_EXE=%JAVA_HOME%/bin/keytool.exe


if exist "%KEYTOOL_EXE%" goto execute

echo. 1>&2
echo ERROR: переменная среды JAVA_HOME установлена к невалидной папке: %JAVA_HOME% 1>&2
echo. 1>&2
echo Пожалуйста укажите в значение переменной среды JAVA_HOME путь куда установлена Java 1>&2

goto fail


:execute
%KEYTOOL_EXE% -import -alias 3x-ui -keystore 3x-ui.jks -storepass %PASSWORD% -file %CERT_FILE% -noprompt

:fail
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
exit /b %EXIT_CODE%
