package dev.jora.postman4j.models;

import java.io.IOException;
import com.fasterxml.jackson.annotation.*;

public enum FormParameterType {
    FILE, TEXT;

    @JsonValue
    public String toValue() {
        switch (this) {
            case FILE: return "file";
            case TEXT: return "text";
        }
        return null;
    }

    @JsonCreator
    public static FormParameterType forValue(String value) throws IOException {
        if (value.equals("file")) return FILE;
        if (value.equals("text")) return TEXT;
        throw new IOException("Cannot deserialize FormParameterType");
    }
}
