package dev.jora.postman4j.models;

import java.io.IOException;
import java.io.IOException;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.fasterxml.jackson.core.type.*;
import lombok.Data;

/**
 * A request represents an HTTP request. If a string, the string is assumed to be the
 * request URL and the method is assumed to be 'GET'.
 */
@Data
@JsonDeserialize(using = RequestUnion.Deserializer.class)
@JsonSerialize(using = RequestUnion.Serializer.class)
public class RequestUnion {
    public RequestClass requestClassValue;
    public String stringValue;

    static class Deserializer extends JsonDeserializer<RequestUnion> {
        @Override
        public RequestUnion deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            RequestUnion value = new RequestUnion();
            switch (jsonParser.currentToken()) {
                case VALUE_NULL:
                    break;
                case VALUE_STRING:
                    String string = jsonParser.readValueAs(String.class);
                    value.stringValue = string;
                    break;
                case START_OBJECT:
                    value.requestClassValue = jsonParser.readValueAs(RequestClass.class);
                    break;
                default: throw new IOException("Cannot deserialize RequestUnion");
            }
            return value;
        }
    }

    static class Serializer extends JsonSerializer<RequestUnion> {
        @Override
        public void serialize(RequestUnion obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (obj.requestClassValue != null) {
                jsonGenerator.writeObject(obj.requestClassValue);
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
