#!/bin/sh

die () {
    echo
    echo "$*"
    echo
    exit 1
} >&2

if [ -z $1 ]; then
    die "Не был указан путь к файлу сертификата для импорта
Используйте:
ImportCert.sh \"путь_к_файлу_сертификата\" [пароль]
   пароль - по умолчанию \"changeme\" (опционально)"
fi

if [ ! -f $1 ]; then
    die "Не удалось найти файл сертификата для импорта
Используйте:
ImportCert.sh \"путь_к_файлу_сертификата\" [пароль]
   пароль - по умолчанию \"changeme\" (опционально)"
fi

CERT_FILE="$1"

if [ -z $2 ]; then
    PASSWORD=changeme
else
    PASSWORD=$2
fi

if [ -z $KEYTOOL_CMD ]; then
  if [ -n $JAVA_HOME ]; then
    KEYTOOL_CMD=$JAVA_HOME/bin/keytool
    if [ -z `$KEYTOOL_CMD` ]; then
        die "Не удалось найти исполняемый файл keytool
Проверьте, верно ли указан путь JAVA_HOME
Также можно указать путь к исполняемому файлу keytool в переменную среды KEYTOOL_CMD"
    fi
  else
    KEYTOOL_CMD=keytool
    if ! command -v keytool; then
        die "Не удалось найти исполняемый файл keytool
Проверьте, верно ли указан путь JAVA_HOME
Также можно указать путь к исполняемому файлу keytool в переменную среды KEYTOOL_CMD"
    fi
  fi
fi

$KEYTOOL_CMD -import -alias 3x-ui -keystore 3x-ui.jks -storepass $PASSWORD -file $CERT_FILE -noprompt