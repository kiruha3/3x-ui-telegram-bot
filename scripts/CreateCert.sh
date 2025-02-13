#!/bin/sh

PRIVATE_KEY_PATH=/etc/ssl/private/3x-ui-custom-private.key
PUBLIC_KEY_PATH=/etc/ssl/certs/3x-ui-custom-public.key
CSR_KEY_PATH=/etc/ssl/private/3x-ui-custom-csr.pem
OPENSSL_CONFIG_FILE=/etc/ssl/openssl_custom_test.cnf

DB_PATH="/etc/x-ui/x-ui.db"

# Функция скопирована с https://raw.githubusercontent.com/cyb3rm4gus/3x-ui-auto_add_ssl/main/3x-ui-autossl.sh
check_sqlite3() {
    if ! command -v sqlite3 &> /dev/null
    then
        echo "sqlite3 не был обнаружен, установка..."
        install_sqlite3
    else
        echo "sqlite3 уже установлен."
    fi
}

# Функция скопирована с https://raw.githubusercontent.com/cyb3rm4gus/3x-ui-auto_add_ssl/main/3x-ui-autossl.sh
install_sqlite3() {
    if [ -x "$(command -v apt-get)" ]; then
        sudo apt-get update -y && sudo apt-get install -y sqlite3
    elif [ -x "$(command -v yum)" ]; then
        sudo yum install -y sqlite
    elif [ -x "$(command -v dnf)" ]; then
        sudo dnf install -y sqlite
    elif [ -x "$(command -v pacman)" ]; then
        sudo pacman -S --noconfirm sqlite
    else
        echo "Менеджер пакетов не найден. Пожалуйста установите sqlite3 вручную."
        exit 1
    fi
}

# Функция скопирована с https://raw.githubusercontent.com/cyb3rm4gus/3x-ui-auto_add_ssl/main/3x-ui-autossl.sh
check_openssl() {
    if ! command -v openssl &> /dev/null
    then
        echo "openssl не был обнаружен, установка..."
        install_openssl
    else
        echo "openssl уже установлен."
    fi
}

# Функция скопирована с https://raw.githubusercontent.com/cyb3rm4gus/3x-ui-auto_add_ssl/main/3x-ui-autossl.sh
install_openssl() {
    if [ -x "$(command -v apt-get)" ]; then
        sudo apt-get update -y && sudo apt-get install -y openssl
    elif [ -x "$(command -v yum)" ]; then
        sudo yum install -y openssl
    elif [ -x "$(command -v dnf)" ]; then
        sudo dnf install -y openssl
    elif [ -x "$(command -v pacman)" ]; then
        sudo pacman -S --noconfirm openssl
    else
        echo "Менеджер пакетов не найден. Пожалуйста установите openssl вручную."
        exit 1
    fi
}

gen_ssl_config_file() {
    IP=`ip address show scope global | grep -Eo 'inet [0-9]{1,3}[.][0-9]{1,3}[.][0-9]{1,3}[.][0-9]{1,3}/[0-9]{1,3}'  | grep -Eo '[0-9]{1,3}[.][0-9]{1,3}[.][0-9]{1,3}[.][0-9]{1,3}'`

    if [ -f $OPENSSL_CONFIG_FILE ]; then
        rm $OPENSSL_CONFIG_FILE
    fi

    echo "[req]
default_bits = 2048
prompt = no
default_md = sha256
distinguished_name = dn
x509_extensions = v3_ca

[dn]
O = My Organization
OU = My Unit
CN = $IP

[v3_ca]
subjectAltName = @alt_names

[alt_names]
DNS.1 = localhost
IP.1 = $IP" > $OPENSSL_CONFIG_FILE
}

generate_ssl_cert() {
    if [ -f $PRIVATE_KEY_PATH ]; then
        rm $PRIVATE_KEY_PATH
    fi
    if [ -f $PUBLIC_KEY_PATH ]; then
        rm $PUBLIC_KEY_PATH
    fi
    if [ -f $CSR_KEY_PATH ]; then
        rm $CSR_KEY_PATH
    fi

    # Создание приватного ключа
    openssl genpkey -algorithm RSA -out $PRIVATE_KEY_PATH

    # Создание запроса на подпись сертификата (CSR)
    openssl req -new -key $PRIVATE_KEY_PATH -out $CSR_KEY_PATH -config $OPENSSL_CONFIG_FILE

    # Создание самоподписанного сертификата с SANs
    openssl x509 -req -in $CSR_KEY_PATH -signkey $PRIVATE_KEY_PATH -out $PUBLIC_KEY_PATH -days 3650 -extensions v3_ca -extfile $OPENSSL_CONFIG_FILE
}

execute_sql_inserts() {
    PUBLIC_KEY_ID=$(sqlite3 "$DB_PATH" "SELECT id FROM settings WHERE key='webCertFile';")

    if [ -x $PUBLIC_KEY_ID ]; then
        PUBLIC_KEY_ID=$(sqlite3 "$DB_PATH" "SELECT IFNULL(MAX(id), 0) FROM settings;")
        PUBLIC_KEY_ID=$((PUBLIC_KEY_ID + 1))
        sqlite3 "$DB_PATH" "INSERT INTO settings VALUES ($PUBLIC_KEY_ID, 'webCertFile', '/etc/ssl/certs/3x-ui-public.key');"
        echo "ID для публичного ключа в таблице настроек $PUBLIC_KEY_ID"
    else
        sqlite3 "$DB_PATH" "UPDATE settings SET value='$PUBLIC_KEY_PATH' WHERE id=$PUBLIC_KEY_ID;"
        echo "ID для публичного ключа в таблице настроек $PUBLIC_KEY_ID"
    fi

    PRIVATE_KEY_ID=$(sqlite3 "$DB_PATH" "SELECT id FROM settings WHERE key='webKeyFile';")
    if [ -x $PRIVATE_KEY_ID ]; then
        PRIVATE_KEY_ID=$(sqlite3 "$DB_PATH" "SELECT IFNULL(MAX(id), 0) FROM settings;")
        PRIVATE_KEY_ID=$((PRIVATE_KEY_ID + 1))
        sqlite3 "$DB_PATH" "INSERT INTO settings VALUES ($PRIVATE_KEY_ID, 'webKeyFile', '/etc/ssl/private/3x-ui-private.key');"
        echo "ID для приватного ключа в таблице настроек $PRIVATE_KEY_ID"
    else
        sqlite3 "$DB_PATH" "UPDATE settings SET value='$PRIVATE_KEY_PATH' WHERE id=$PRIVATE_KEY_ID;"
        echo "ID для приватного ключа в таблице настроек $PRIVATE_KEY_ID"
    fi
}

check_sqlite3
check_openssl
gen_ssl_config_file
generate_ssl_cert

execute_sql_inserts