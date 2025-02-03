## Запуск
1. Скачать последнюю [релизную версию](https://github.com/Alemakave/3x-ui-telegram-bot/releases) `3x-ut-telegram-bot.jar`<br>
2. Создать конфигурационный файл `application.yml` и заполнить<br>

  ```yaml 
threex:
    panel:
      ip: 10.10.10.10
      port: 5678
      path: bibBoBbibO
      username: admin
      password: admin
    # Прокси необходим, если бот запускается из RU региона
    connection:
      proxy:
        address: 127.0.0.1
        port: 12334
telegram:
    bot:
      token: 0000000000:AABBRRc_xyzabcdEfgHigklmNOPqrstu13
  ```

3. Запустить бота через командную строку:<br>
   `java -jar 3x-ut-telegram-bot.jar`

## TODO реализации API

<details>
  <summary>Нажмите для получения информации о маршрутах API и статусе реализации</summary>

#### Использование и статус реализации
- `/login` с `POST`-данными: `{username: '', password: ''}` для входа
- `/panel/api/inbounds` это базовый путь для следующих действий:

| Метод  | Путь                               | Описание                                                                                        |    Статус реализации    |
|:------:|------------------------------------|-------------------------------------------------------------------------------------------------|:-----------------------:|
| `GET`  | `"/list"`                          | Получить список всех подключений                                                                |   :white_check_mark:    |
| `GET`  | `"/get/:id"`                       | Получить информацию о подключении по его id                                                     |   :white_check_mark:    |
| `GET`  | `"/getClientTraffics/:email"`      | Получить трафик клиента по его email                                                            |  :white_square_button:  |
| `GET`  | `"/getClientTrafficsById/:id"`     | Получить трафик клиентов по id                                                                  |  :white_square_button:  |
| `GET`  | `"/createbackup"`                  | Отправить бекап админам в телеграм бот                                                          |  :white_square_button:  |
| `POST` | `"/add"`                           | Добавить подключение                                                                            |   :white_check_mark:    |
| `POST` | `"/del/:id"`                       | Удалить подключение по его id                                                                   |   :white_check_mark:    |
| `POST` | `"/update/:id"`                    | Обновить подключение по его id                                                                  |   :white_check_mark:    |
| `POST` | `"/clientIps/:email"`              | Получить IP адреса клиентов по его email                                                        |  :white_square_button:  |
| `POST` | `"/clearClientIps/:email"`         | Отчистить IP адреса клиентов по его email                                                       |  :white_square_button:  |
| `POST` | `"/addClient"`                     | Добавить клиента в подключение                                                                  |   :white_check_mark:    |
| `POST` | `"/:id/delClient/:clientId"`       | Удалить клиента по его clientId\* в подключении по id                                           |  :white_square_button:  |
| `POST` | `"/updateClient/:clientId"`        | Обновить клиента по его clientId\*                                                              |  :white_square_button:  |
| `POST` | `"/:id/resetClientTraffic/:email"` | Сбросить трафик у клиента                                                                       |  :white_square_button:  |
| `POST` | `"/resetAllTraffics"`              | Сбросить трафик у всех подключений                                                              |  :white_square_button:  |
| `POST` | `"/resetAllClientTraffics/:id"`    | Сбросить трафик у всех клиентов в подключении по его id                                         |  :white_square_button:  |
| `POST` | `"/delDepletedClients/:id"`        | Удалить всех клиентов в подключении id (-1 для всех подключений) с истекшем сроком или трафиком |  :white_square_button:  |

\* - Поле `clientId` должно быть заполнено следующим образом:

- `client.id` для VMESS и VLESS
- `client.password` для TROJAN
- `client.email` для Shadowsocks


:white_square_button: - Не реализовано<br>
:ballot_box_with_check: - В процессе<br>
:white_check_mark: - Реализовано
</details>