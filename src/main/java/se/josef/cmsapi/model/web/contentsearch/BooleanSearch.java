package se.josef.cmsapi.model.web.contentsearch;

import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.query.Criteria;

@EqualsAndHashCode(callSuper = true)
public class BooleanSearch extends ContentSearch<Boolean> {

    public BooleanSearch() {
    }

    public BooleanSearch(String name, Boolean parameters) {
        super(name, parameters);
    }

    @Override
    public Criteria getCriteria() {
        return Criteria
                .where("contentFields")
                .elemMatch(
                        Criteria.where("name").is(getName())
                                .and("data").is(getParameters())
                );

    }
}
