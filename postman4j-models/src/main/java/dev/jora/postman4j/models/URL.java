package dev.jora.postman4j.models;

import java.io.IOException;
import java.io.IOException;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.fasterxml.jackson.core.type.*;
import lombok.Data;

/**
 * If object, contains the complete broken-down URL for this request. If string, contains
 * the literal request URL.
 */
@Data
@JsonDeserialize(using = URL.Deserializer.class)
@JsonSerialize(using = URL.Serializer.class)
public class URL {
    public URLClass urlClassValue;
    public String stringValue;

    static class Deserializer extends JsonDeserializer<URL> {
        @Override
        public URL deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            URL value = new URL();
            switch (jsonParser.currentToken()) {
                case VALUE_NULL:
                    break;
                case VALUE_STRING:
                    String string = jsonParser.readValueAs(String.class);
                    value.stringValue = string;
                    break;
                case START_OBJECT:
                    value.urlClassValue = jsonParser.readValueAs(URLClass.class);
                    break;
                default: throw new IOException("Cannot deserialize URL");
            }
            return value;
        }
    }

    static class Serializer extends JsonSerializer<URL> {
        @Override
        public void serialize(URL obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (obj.urlClassValue != null) {
                jsonGenerator.writeObject(obj.urlClassValue);
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
