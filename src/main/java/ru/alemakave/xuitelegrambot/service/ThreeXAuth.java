package ru.alemakave.xuitelegrambot.service;

import org.springframework.http.HttpMethod;

public interface ThreeXAuth {
    /**
     * <p>
     *    <b><i>Метод</i></b>: {@link HttpMethod#POST}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>: {@code /login}
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот путь используется для аутентификации пользователей и создания идентификатора
     *     сеанса, хранящегося в файле cookie с именем «сессия», что обеспечивает авторизованный доступ для выполнения
     *     различных операций на панели.
     * </p>
     */
    void login();

    /**
     * @return Статус авторизации
     */
    boolean isAuthorized();
}
