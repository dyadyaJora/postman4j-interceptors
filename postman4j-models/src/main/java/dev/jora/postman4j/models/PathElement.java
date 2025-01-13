package dev.jora.postman4j.models;

import java.io.IOException;
import java.io.IOException;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.fasterxml.jackson.core.type.*;

/**
 * The complete path of the current url, broken down into segments. A segment could be a
 * string, or a path variable.
 */
@JsonDeserialize(using = PathElement.Deserializer.class)
@JsonSerialize(using = PathElement.Serializer.class)
public class PathElement {
    public PathClass pathClassValue;
    public String stringValue;

    static class Deserializer extends JsonDeserializer<PathElement> {
        @Override
        public PathElement deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            PathElement value = new PathElement();
            switch (jsonParser.currentToken()) {
                case VALUE_STRING:
                    String string = jsonParser.readValueAs(String.class);
                    value.stringValue = string;
                    break;
                case START_OBJECT:
                    value.pathClassValue = jsonParser.readValueAs(PathClass.class);
                    break;
                default: throw new IOException("Cannot deserialize PathElement");
            }
            return value;
        }
    }

    static class Serializer extends JsonSerializer<PathElement> {
        @Override
        public void serialize(PathElement obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (obj.pathClassValue != null) {
                jsonGenerator.writeObject(obj.pathClassValue);
                return;
            }
            if (obj.stringValue != null) {
                jsonGenerator.writeObject(obj.stringValue);
                return;
            }
            throw new IOException("PathElement must not be null");
        }
    }
}
