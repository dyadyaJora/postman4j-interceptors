package dev.jora.postman4j.models;

import java.io.IOException;
import java.io.IOException;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.fasterxml.jackson.core.type.*;

/**
 * Postman allows you to version your collections as they grow, and this field holds the
 * version number. While optional, it is recommended that you use this field to its fullest
 * extent!
 */
@JsonDeserialize(using = CollectionVersion.Deserializer.class)
@JsonSerialize(using = CollectionVersion.Serializer.class)
public class CollectionVersion {
    public CollectionVersionClass collectionVersionClassValue;
    public String stringValue;

    static class Deserializer extends JsonDeserializer<CollectionVersion> {
        @Override
        public CollectionVersion deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            CollectionVersion value = new CollectionVersion();
            switch (jsonParser.currentToken()) {
                case VALUE_NULL:
                    break;
                case VALUE_STRING:
                    String string = jsonParser.readValueAs(String.class);
                    value.stringValue = string;
                    break;
                case START_OBJECT:
                    value.collectionVersionClassValue = jsonParser.readValueAs(CollectionVersionClass.class);
                    break;
                default: throw new IOException("Cannot deserialize CollectionVersion");
            }
            return value;
        }
    }

    static class Serializer extends JsonSerializer<CollectionVersion> {
        @Override
        public void serialize(CollectionVersion obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (obj.collectionVersionClassValue != null) {
                jsonGenerator.writeObject(obj.collectionVersionClassValue);
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
