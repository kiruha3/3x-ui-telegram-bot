package ru.alemakave.xuitelegrambot.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.alemakave.xuitelegrambot.json.deserializer.FlowDeserializer;
import ru.alemakave.xuitelegrambot.json.serializer.FlowSerializer;

@JsonSerialize(using = FlowSerializer.class)
@JsonDeserialize(using = FlowDeserializer.class)
public enum Flow {
    NONE,
    XTLS_RPRX_VISION,
    XTLS_RPRX_VISION_UDP443;

    public static Flow defaultValue() {
        return XTLS_RPRX_VISION;
    }

    @Override
    public String toString() {
        if (this == NONE) {
            return "";
        }

        return super.toString().toLowerCase().replace('_', '-');
    }
}
