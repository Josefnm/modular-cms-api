package se.josef.cmsapi.model.web.contentsearch;

import org.springframework.data.mongodb.core.query.Criteria;
import se.josef.cmsapi.model.web.contentsearch.parameter.RangeParameter;

public class utils {
    public static <T> Criteria getRangeCriteria(Criteria criteriaStub, RangeParameter<T> rangeParameter) {

        if (rangeParameter.getLessThan() != null) {
            criteriaStub.lt(rangeParameter.getLessThan());
        }
        if (rangeParameter.getMoreThan() != null) {
            criteriaStub.gt(rangeParameter.getMoreThan());
        }
        return criteriaStub;
    }

}
