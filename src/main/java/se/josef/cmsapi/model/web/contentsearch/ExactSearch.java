package se.josef.cmsapi.model.web.contentsearch;

import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Used for exact matches on Content fields
 */
@EqualsAndHashCode(callSuper = true)
public class ExactSearch<T> extends ContentSearch<T> {

    public ExactSearch(String name, T parameters) {
        super(name, parameters);
    }

    @Override
    public Criteria getCriteria() {
        return Criteria.where(getName()).is(getParameters());
    }
}
