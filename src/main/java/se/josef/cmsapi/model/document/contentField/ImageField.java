package se.josef.cmsapi.model.document.contentField;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ImageField extends ContentField<String> {
    public ImageField(String name, String data) {
        super(name, data);
    }
}
