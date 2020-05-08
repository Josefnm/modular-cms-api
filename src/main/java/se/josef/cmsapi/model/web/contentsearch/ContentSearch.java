package se.josef.cmsapi.model.web.contentsearch;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;


/**
 * Deserialization to correct subclass handled by annotations
 *
 * @param <T>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = Id.NAME,
        property = "type")
@JsonSubTypes({
        @Type(value = BooleanSearch.class),
        @Type(value = DateSearch.class),
        @Type(value = ModuleSearch.class),
        @Type(value = NumberSearch.class),
        @Type(value = StringSearch.class),
        @Type(value = CreatedSearch.class),
        @Type(value = PublicSearch.class),
        @Type(value = NameSearch.class),
})
public abstract class ContentSearch<T> {
    private String name;
    private T parameters;

    // Used to build a search query. Each impl must return a valid search Criteria
    public abstract Criteria getCriteria();
}
