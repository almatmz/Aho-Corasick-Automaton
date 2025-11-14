package almat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility for JSON read/write using Jackson.
 */
public final class JsonIO {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private JsonIO() {}

    public static <T> T read(Path path, Class<T> clazz) throws IOException {
        return MAPPER.readValue(Files.readString(path), clazz);
    }

    public static void write(Path path, Object value) throws IOException {
        String json = MAPPER.writeValueAsString(value);
        Files.writeString(path, json);
    }
}