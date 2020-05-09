package se.josef.cmsapi.model.web.contentsearch.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RangeParameter<T> {
    private T moreThan;
    private T lessThan;
}
