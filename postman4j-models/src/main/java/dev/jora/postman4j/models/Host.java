package dev.jora.postman4j.models;

import java.io.IOException;
import java.io.IOException;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.fasterxml.jackson.core.type.*;
import java.util.List;

/**
 * The host for the URL, E.g: api.yourdomain.com. Can be stored as a string or as an array
 * of strings.
 */
@JsonDeserialize(using = Host.Deserializer.class)
@JsonSerialize(using = Host.Serializer.class)
public class Host {
    public List<String> stringArrayValue;
    public String stringValue;

    static class Deserializer extends JsonDeserializer<Host> {
        @Override
        public Host deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            Host value = new Host();
            switch (jsonParser.currentToken()) {
                case VALUE_NULL:
                    break;
                case VALUE_STRING:
                    String string = jsonParser.readValueAs(String.class);
                    value.stringValue = string;
                    break;
                case START_ARRAY:
                    value.stringArrayValue = jsonParser.readValueAs(new TypeReference<List>() {});
                    break;
                default: throw new IOException("Cannot deserialize Host");
            }
            return value;
        }
    }

    static class Serializer extends JsonSerializer<Host> {
        @Override
        public void serialize(Host obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (obj.stringArrayValue != null) {
                jsonGenerator.writeObject(obj.stringArrayValue);
                return;
            }
            if (obj.stringValue != null) {
                jsonGenerator.writeObject(obj.stringValue);
                return;
            }
            jsonGenerator.writeNull();
        }
    }
}
