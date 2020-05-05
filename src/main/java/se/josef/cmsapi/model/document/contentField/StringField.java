package se.josef.cmsapi.model.document.contentField;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class StringField extends ContentField<String> {

    public StringField() {
    }

    public StringField(String name, String data) {
        super(name, data);
    }
}
