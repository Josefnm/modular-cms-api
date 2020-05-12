package se.josef.cmsapi.model.web.contentsearch;

import lombok.EqualsAndHashCode;
import se.josef.cmsapi.model.web.contentsearch.parameter.RangeParameter;

@EqualsAndHashCode(callSuper = true)
public class FieldNumberSearch extends RangeSearch<Long> {

    public FieldNumberSearch(String name, RangeParameter<Long> parameters) {
        super(name, parameters);
    }
}
