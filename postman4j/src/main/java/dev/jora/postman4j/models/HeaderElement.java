package dev.jora.postman4j.models;

import java.io.IOException;
import java.io.IOException;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.fasterxml.jackson.core.type.*;

/**
 * No HTTP request is complete without its headers, and the same is true for a Postman
 * request. This field is an array containing all the headers.
 */
@JsonDeserialize(using = HeaderElement.Deserializer.class)
@JsonSerialize(using = HeaderElement.Serializer.class)
public class HeaderElement {
    public Header headerValue;
    public String stringValue;

    static class Deserializer extends JsonDeserializer<HeaderElement> {
        @Override
        public HeaderElement deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            HeaderElement value = new HeaderElement();
            switch (jsonParser.currentToken()) {
                case VALUE_STRING:
                    String string = jsonParser.readValueAs(String.class);
                    value.stringValue = string;
                    break;
                case START_OBJECT:
                    value.headerValue = jsonParser.readValueAs(Header.class);
                    break;
                default: throw new IOException("Cannot deserialize HeaderElement");
            }
            return value;
        }
    }

    static class Serializer extends JsonSerializer<HeaderElement> {
        @Override
        public void serialize(HeaderElement obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (obj.headerValue != null) {
                jsonGenerator.writeObject(obj.headerValue);
                return;
            }
            if (obj.stringValue != null) {
                jsonGenerator.writeObject(obj.stringValue);
                return;
            }
            throw new IOException("HeaderElement must not be null");
        }
    }
}
