package se.josef.cmsapi.config;
/*

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import se.josef.cmsapi.enums.DataType;
import se.josef.cmsapi.model.document.Content;
import se.josef.cmsapi.model.document.ContentField;

import java.io.IOException;

public class ContentFieldDeserializer extends StdDeserializer<ContentField<?>> {


    public ContentFieldDeserializer() {
        this(null);
    }

    public ContentFieldDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ContentField<?> deserialize(JsonParser jp, DeserializationContext context)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        String id = node.get("name").asText();
        Boolean required = node.get("required").asBoolean();
        DataType dataType= DataType.valueOf(node.get("dataType").asText());

        node.get("data").

        return new Content(id, itemName, new Content(userId, null));
    }


}*/

