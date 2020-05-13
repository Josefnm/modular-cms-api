package se.josef.cmsapi.model.document.contentField;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;


/**
 * Deserialization to correct subclass handled by annotations
 *  This class describes the data contained by Content documents
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = Id.NAME,
        property = "type")
@JsonSubTypes({
        @Type(value = BooleanField.class),
        @Type(value = DateField.class),
        @Type(value = ImageField.class),
        @Type(value = ModuleField.class),
        @Type(value = NumberField.class),
        @Type(value = StringField.class),
        @Type(value = TextField.class)
})
public abstract class ContentField<T> {
    @Indexed
    private String name;
    @Indexed
    private T data;
}
