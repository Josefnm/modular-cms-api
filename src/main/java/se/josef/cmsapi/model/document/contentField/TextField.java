package se.josef.cmsapi.model.document.contentField;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class TextField extends ContentField<String> {
    public TextField(String name, String data) {
        super(name, data);
    }
}
