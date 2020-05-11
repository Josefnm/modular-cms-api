package se.josef.cmsapi.model.web.contentsearch;

import org.springframework.data.mongodb.core.query.Criteria;
import se.josef.cmsapi.model.web.contentsearch.parameter.RangeParameter;

public abstract class RangeSearch<T> extends ContentSearch<RangeParameter<T>> {
    public RangeSearch() {
    }

    public RangeSearch(String name, RangeParameter<T> parameters) {
        super(name, parameters);
    }

    public Criteria getCriteria() {

        var lessThan = getParameters().getLessThan();
        var moreThan = getParameters().getMoreThan();

        var elemCriteria = Criteria.where("name").is(getName()).and("data");

        if (lessThan != null) {
            elemCriteria.lt(lessThan);
        }
        if (moreThan != null) {
            elemCriteria.gt(moreThan);
        }

        return Criteria.where("contentFields").elemMatch(elemCriteria);
    }
}
