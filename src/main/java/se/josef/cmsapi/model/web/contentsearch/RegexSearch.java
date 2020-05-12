package se.josef.cmsapi.model.web.contentsearch;

import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.query.Criteria;

@EqualsAndHashCode(callSuper = true)
public class RegexSearch extends ContentSearch<String> {

    public RegexSearch(String name, String parameters) {
        super(name, parameters);
    }

    @Override
    public Criteria getCriteria() {
        return Criteria.where(getName()).regex(getParameters(), "i");
    }
}
