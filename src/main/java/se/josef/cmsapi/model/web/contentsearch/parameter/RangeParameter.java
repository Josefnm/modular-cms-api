package se.josef.cmsapi.model.web.contentsearch.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Parameter for searching values like dates and numbers
 * Can safely be initialized with both or either but not neither
 */
@Data
@AllArgsConstructor
public class RangeParameter<T> {
    private T moreThan;
    private T lessThan;
}
