package se.josef.cmsapi.model.web.contentsearch;

import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.query.Criteria;

@EqualsAndHashCode(callSuper = true)
public class NameSearch extends ContentSearch<String> {

    public NameSearch() {
    }

    public NameSearch(String name, String parameters) {
        super(name, parameters);
    }

    @Override
    public Criteria getCriteria() {
        return Criteria.where("name").regex(getParameters(), "i");
    }
}
