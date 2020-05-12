package se.josef.cmsapi.model.web.contentsearch.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * parameter for searching values like dates and numbers
 * @param <T>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RangeParameter<T> {
    private T moreThan;
    private T lessThan;
}
