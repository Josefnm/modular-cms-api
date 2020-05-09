package se.josef.cmsapi.model.web.contentsearch;

import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.query.Criteria;

@EqualsAndHashCode(callSuper = true)
public class PublicSearch extends ContentSearch<Boolean> {

    public PublicSearch() {
    }

    public PublicSearch( Boolean parameters) {
        super(null, parameters);
    }

    @Override
    public Criteria getCriteria() {
        return Criteria.where("isPublic").is(getParameters());
    }
}
