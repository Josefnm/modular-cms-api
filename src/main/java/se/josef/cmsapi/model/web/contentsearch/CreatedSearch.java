package se.josef.cmsapi.model.web.contentsearch;

import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.query.Criteria;
import se.josef.cmsapi.model.web.contentsearch.parameter.RangeParameter;

import java.util.Date;

import static se.josef.cmsapi.model.web.contentsearch.utils.getRangeCriteria;

@EqualsAndHashCode(callSuper = true)
public class CreatedSearch extends ContentSearch<RangeParameter<Date>> {
    public CreatedSearch() {
    }

    public CreatedSearch(String name, RangeParameter<Date> parameters) {
        super(name, parameters);
    }

    public Criteria getCriteria() {
        var criteriaStub = Criteria.where(getName());
        return getRangeCriteria(criteriaStub, getParameters());
    }

}
