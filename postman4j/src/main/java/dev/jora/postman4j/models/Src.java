package dev.jora.postman4j.models;

import java.io.IOException;
import java.io.IOException;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.fasterxml.jackson.core.type.*;
import java.util.List;

@JsonDeserialize(using = Src.Deserializer.class)
@JsonSerialize(using = Src.Serializer.class)
public class Src {
    public String stringValue;
    public List<Object> anythingArrayValue;

    static class Deserializer extends JsonDeserializer<Src> {
        @Override
        public Src deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            Src value = new Src();
            switch (jsonParser.currentToken()) {
                case VALUE_NULL:
                    break;
                case VALUE_STRING:
                    String string = jsonParser.readValueAs(String.class);
                    value.stringValue = string;
                    break;
                case START_ARRAY:
                    value.anythingArrayValue = jsonParser.readValueAs(new TypeReference<List>() {});
                    break;
                default: throw new IOException("Cannot deserialize Src");
            }
            return value;
        }
    }

    static class Serializer extends JsonSerializer<Src> {
        @Override
        public void serialize(Src obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (obj.stringValue != null) {
                jsonGenerator.writeObject(obj.stringValue);
                return;
            }
            if (obj.anythingArrayValue != null) {
                jsonGenerator.writeObject(obj.anythingArrayValue);
                return;
            }
            jsonGenerator.writeNull();
        }
    }
}
