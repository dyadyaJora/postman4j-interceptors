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
@JsonDeserialize(using = HeaderUnion.Deserializer.class)
@JsonSerialize(using = HeaderUnion.Serializer.class)
public class HeaderUnion {
    public List<Header> headerArrayValue;
    public String stringValue;

    static class Deserializer extends JsonDeserializer<HeaderUnion> {
        @Override
        public HeaderUnion deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            HeaderUnion value = new HeaderUnion();
            switch (jsonParser.currentToken()) {
                case VALUE_NULL:
                    break;
                case VALUE_STRING:
                    String string = jsonParser.readValueAs(String.class);
                    value.stringValue = string;
                    break;
                case START_ARRAY:
                    value.headerArrayValue = jsonParser.readValueAs(new TypeReference<List>() {});
                    break;
                default: throw new IOException("Cannot deserialize HeaderUnion");
            }
            return value;
        }
    }

    static class Serializer extends JsonSerializer<HeaderUnion> {
        @Override
        public void serialize(HeaderUnion obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (obj.headerArrayValue != null) {
                jsonGenerator.writeObject(obj.headerArrayValue);
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
