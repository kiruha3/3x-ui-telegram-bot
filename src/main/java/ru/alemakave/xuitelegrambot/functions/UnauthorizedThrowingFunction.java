package ru.alemakave.xuitelegrambot.functions;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.function.ThrowingFunction;
import reactor.core.publisher.Mono;
import ru.alemakave.xuitelegrambot.exception.UnauthorizedException;

@Slf4j
public class UnauthorizedThrowingFunction<T> implements ThrowingFunction<Throwable, Mono<? extends T>> {
    @NotNull
    @Override
    public Mono<T> applyWithException(@NotNull Throwable throwable) {
        if (throwable instanceof UnauthorizedException) {
            log.error(throwable.getMessage());
            return Mono.empty();
        }

        return Mono.error(throwable);
    }
}
