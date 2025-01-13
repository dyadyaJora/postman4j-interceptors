package dev.jora.postman4j.models;

import java.io.IOException;
import java.io.IOException;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.fasterxml.jackson.core.type.*;

/**
 * The time taken by the request to complete. If a number, the unit is milliseconds. If the
 * response is manually created, this can be set to `null`.
 */
@JsonDeserialize(using = ResponseTime.Deserializer.class)
@JsonSerialize(using = ResponseTime.Serializer.class)
public class ResponseTime {
    public Double doubleValue;
    public String stringValue;

    static class Deserializer extends JsonDeserializer<ResponseTime> {
        @Override
        public ResponseTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            ResponseTime value = new ResponseTime();
            switch (jsonParser.currentToken()) {
                case VALUE_NULL:
                    break;
                case VALUE_NUMBER_INT:
                case VALUE_NUMBER_FLOAT:
                    value.doubleValue = jsonParser.readValueAs(Double.class);
                    break;
                case VALUE_STRING:
                    String string = jsonParser.readValueAs(String.class);
                    value.stringValue = string;
                    break;
                default: throw new IOException("Cannot deserialize ResponseTime");
            }
            return value;
        }
    }

    static class Serializer extends JsonSerializer<ResponseTime> {
        @Override
        public void serialize(ResponseTime obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (obj.doubleValue != null) {
                jsonGenerator.writeObject(obj.doubleValue);
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
