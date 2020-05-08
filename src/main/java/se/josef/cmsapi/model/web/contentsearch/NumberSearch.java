package se.josef.cmsapi.model.web.contentsearch;

import lombok.EqualsAndHashCode;
import se.josef.cmsapi.model.web.contentsearch.parameter.RangeParameter;

@EqualsAndHashCode(callSuper = true)
public class NumberSearch extends RangeSearch<Long> {
    public NumberSearch() {
    }

    public NumberSearch(String name, RangeParameter<Long> parameters) {
        super(name, parameters);
    }
}
