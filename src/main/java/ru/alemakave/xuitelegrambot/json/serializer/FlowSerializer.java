package ru.alemakave.xuitelegrambot.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import ru.alemakave.xuitelegrambot.model.Flow;

import java.io.IOException;

public class FlowSerializer extends StdSerializer<Flow> {
    public FlowSerializer() {
        this(null);
    }

    protected FlowSerializer(Class<Flow> t) {
        super(t);
    }

    @Override
    public void serialize(Flow value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == Flow.NONE) {
            gen.writeString("");
        } else {
            gen.writeString(value.toString());
        }
    }
}
