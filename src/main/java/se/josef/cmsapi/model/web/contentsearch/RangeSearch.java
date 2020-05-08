package se.josef.cmsapi.model.web.contentsearch;

import org.springframework.data.mongodb.core.query.Criteria;
import se.josef.cmsapi.model.web.contentsearch.parameter.RangeParameter;

import static se.josef.cmsapi.model.web.contentsearch.utils.getRangeCriteria;

public abstract class RangeSearch<T> extends ContentSearch<RangeParameter<T>> {
    public RangeSearch() {
    }

    public RangeSearch(String name, RangeParameter<T> parameters) {
        super(name, parameters);
    }

    public Criteria getCriteria() {
        var criteriaStub = Criteria.where("name").is(getName()).and("data");

        var elemCriteria = getRangeCriteria(criteriaStub, getParameters());

        return Criteria.where("contentFields").elemMatch(elemCriteria);
    }
}
