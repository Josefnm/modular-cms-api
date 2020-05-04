package se.josef.cmsapi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JsonUtils {

    private final ObjectMapper objectMapper;

    public JsonUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * @param json
     * @param <K>  key
     * @param <V>  value
     * @return typed map of json string
     * @throws JsonProcessingException
     */
    public <K, V> Map<K, V> getJsonAsMap(String json) throws JsonProcessingException {

        TypeReference<Map<K, V>> typeRef = new TypeReference<>() {
        };

        return objectMapper.readValue(json, typeRef);
    }
}
