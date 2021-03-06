package se.josef.cmsapi.model.web.contentsearch;


import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.query.Criteria;

@EqualsAndHashCode(callSuper = true)
public class FieldExactSearch<T> extends ContentSearch<T> {

    public FieldExactSearch(String name, T parameters) {
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
