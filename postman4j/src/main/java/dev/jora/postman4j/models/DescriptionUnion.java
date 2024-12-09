package dev.jora.postman4j.models;

import java.io.IOException;
import java.io.IOException;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.fasterxml.jackson.core.type.*;

/**
 * A Description can be a raw text, or be an object, which holds the description along with
 * its format.
 */
@JsonDeserialize(using = DescriptionUnion.Deserializer.class)
@JsonSerialize(using = DescriptionUnion.Serializer.class)
public class DescriptionUnion {
    public Description descriptionValue;
    public String stringValue;

    static class Deserializer extends JsonDeserializer<DescriptionUnion> {
        @Override
        public DescriptionUnion deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            DescriptionUnion value = new DescriptionUnion();
            switch (jsonParser.currentToken()) {
                case VALUE_NULL:
                    break;
                case VALUE_STRING:
                    String string = jsonParser.readValueAs(String.class);
                    value.stringValue = string;
                    break;
                case START_OBJECT:
                    value.descriptionValue = jsonParser.readValueAs(Description.class);
                    break;
                default: throw new IOException("Cannot deserialize DescriptionUnion");
            }
            return value;
        }
    }

    static class Serializer extends JsonSerializer<DescriptionUnion> {
        @Override
        public void serialize(DescriptionUnion obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (obj.descriptionValue != null) {
                jsonGenerator.writeObject(obj.descriptionValue);
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
