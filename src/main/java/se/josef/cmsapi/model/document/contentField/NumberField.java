package se.josef.cmsapi.model.document.contentField;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class NumberField extends ContentField<Long> {
    public NumberField() {
    }

    public NumberField(String name, Long data) {
        super(name, data);
    }
}
