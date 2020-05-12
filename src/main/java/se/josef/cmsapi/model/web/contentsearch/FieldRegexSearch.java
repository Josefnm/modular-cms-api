package se.josef.cmsapi.model.web.contentsearch;

import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.query.Criteria;

@EqualsAndHashCode(callSuper = true)
public class FieldRegexSearch extends ContentSearch<String> {

    public FieldRegexSearch(String name, String parameters) {
        super(name, parameters);
    }

    @Override
    public Criteria getCriteria() {
        return Criteria
                .where("contentFields")
                .elemMatch(
                        Criteria.where("name").is(getName())
                                .and("data").regex(getParameters(), "i")
                );
    }
}
