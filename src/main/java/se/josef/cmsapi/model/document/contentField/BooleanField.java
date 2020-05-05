package se.josef.cmsapi.model.document.contentField;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class BooleanField extends ContentField<Boolean> {

    public BooleanField() {
    }

    public BooleanField(String name, Boolean data) {
        super(name, data);
    }
}
