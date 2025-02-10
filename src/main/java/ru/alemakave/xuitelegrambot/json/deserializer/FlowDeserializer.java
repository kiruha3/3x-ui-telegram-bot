package ru.alemakave.xuitelegrambot.json.deserializer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ru.alemakave.xuitelegrambot.model.Flow;

import java.io.IOException;

public class FlowDeserializer extends StdDeserializer<Flow> {
    public FlowDeserializer() {
        this(null);
    }

    protected FlowDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Flow deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String flowStr = jp.getCodec().readValue(jp, String.class);

        return switch (flowStr) {
            case "" -> Flow.NONE;
            case "xtls-rprx-vision" -> Flow.XTLS_RPRX_VISION;
            case "xtls-rprx-vision-udp443" -> Flow.XTLS_RPRX_VISION_UDP443;
            default -> throw new JsonParseException(jp, "Unsupported flow: " + flowStr);
        };
    }
}
