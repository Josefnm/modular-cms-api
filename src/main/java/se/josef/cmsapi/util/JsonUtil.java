package se.josef.cmsapi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JsonUtil {

    private final ObjectMapper objectMapper;

    public JsonUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <K, V> Map<K, V> getJsonAsMap(String json) throws JsonProcessingException {

        TypeReference<Map<K, V>> typeRef = new TypeReference<>() {
        };

        return objectMapper.readValue(json, typeRef);
    }
}
