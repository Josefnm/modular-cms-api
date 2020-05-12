package se.josef.cmsapi.model.web.contentsearch.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * parameter for searching values like dates and numbers
 * @param <T>
 */
@Data
@AllArgsConstructor
public class RangeParameter<T> {
    private T moreThan;
    private T lessThan;
}
