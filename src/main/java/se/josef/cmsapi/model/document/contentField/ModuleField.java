package se.josef.cmsapi.model.document.contentField;


import lombok.EqualsAndHashCode;
import se.josef.cmsapi.model.document.Content;

@EqualsAndHashCode(callSuper = true)
public class ModuleField extends ContentField<String> {

    public ModuleField() {
    }

    public ModuleField(String name, String data) {
        super(name, data);
    }
}
