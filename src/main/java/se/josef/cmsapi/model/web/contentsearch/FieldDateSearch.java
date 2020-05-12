package se.josef.cmsapi.model.web.contentsearch;

import lombok.EqualsAndHashCode;
import se.josef.cmsapi.model.web.contentsearch.parameter.RangeParameter;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
public class FieldDateSearch extends RangeSearch<Date> {

    public FieldDateSearch(String name, RangeParameter<Date> parameters) {
        super(name, parameters);
    }

}
