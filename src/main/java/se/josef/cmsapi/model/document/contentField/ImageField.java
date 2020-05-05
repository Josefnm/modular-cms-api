package se.josef.cmsapi.model.document.contentField;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ImageField extends ContentField<String> {

    public ImageField() {
    }

    public ImageField(String name, String data) {
        super(name, data);
    }
}
