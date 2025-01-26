package ru.alemakave.xuitelegrambot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ThreeXWebImpl implements ThreeXWeb {
    @Override
    public void createBackup() {

    }

    @Override
    public void resetAllTraffics() {

    }
}
