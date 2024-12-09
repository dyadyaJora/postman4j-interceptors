package dev.jora.postman4j.models;

import java.io.IOException;
import java.io.IOException;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.fasterxml.jackson.core.type.*;
import lombok.Data;

import java.util.List;

@Data
@JsonDeserialize(using = Headers.Deserializer.class)
@JsonSerialize(using = Headers.Serializer.class)
public class Headers {
    public List<HeaderElement> unionArrayValue;
    public String stringValue;

    static class Deserializer extends JsonDeserializer<Headers> {
        @Override
        public Headers deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            Headers value = new Headers();
            switch (jsonParser.currentToken()) {
                case VALUE_NULL:
                    break;
                case VALUE_STRING:
                    String string = jsonParser.readValueAs(String.class);
                    value.stringValue = string;
                    break;
                case START_ARRAY:
                    value.unionArrayValue = jsonParser.readValueAs(new TypeReference<List>() {});
                    break;
                default: throw new IOException("Cannot deserialize Headers");
            }
            return value;
        }
    }

    static class Serializer extends JsonSerializer<Headers> {
        @Override
        public void serialize(Headers obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (obj.unionArrayValue != null) {
                jsonGenerator.writeObject(obj.unionArrayValue);
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
