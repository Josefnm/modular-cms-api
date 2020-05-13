package se.josef.cmsapi.model.web.contentsearch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;


/**
 * Deserialization to correct subclass handled by annotations with the type property.
 * Each subclass handles how to create a search criteria. A list of these can then be used to
 * make advanced searches.
 * Subclasses named Field are for searching on ContentFields, the other subclasses are for searching on the
 * contents other properties
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = Id.NAME,
        property = "type")
@JsonSubTypes({
        @Type(value = FieldExactSearch.class),
        @Type(value = DateSearch.class),
        @Type(value = FieldDateSearch.class),
        @Type(value = RegexSearch.class),
        @Type(value = FieldNumberSearch.class),
        @Type(value = FieldRegexSearch.class),
        @Type(value = ExactSearch.class),
})
public abstract class ContentSearch<T> {
    private String name;
    private T parameters;

    /**
     * builds a query criteria from the name and parameters
     */
    @JsonIgnore
    public abstract Criteria getCriteria();
}
