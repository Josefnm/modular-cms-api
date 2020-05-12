package se.josef.cmsapi.model.web.contentsearch;

import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.query.Criteria;
import se.josef.cmsapi.model.web.contentsearch.parameter.RangeParameter;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
public class DateSearch extends ContentSearch<RangeParameter<Date>> {

    public DateSearch(String name, RangeParameter<Date> parameters) {
        super(name, parameters);
    }

    public Criteria getCriteria() {

        var lessThan = getParameters().getLessThan();
        var moreThan = getParameters().getMoreThan();
        var criteria = Criteria.where(getName());

        if (lessThan != null) {
            criteria.lt(lessThan);
        }
        if (moreThan != null) {
            criteria.gt(moreThan);
        }

        return criteria;
    }

}
