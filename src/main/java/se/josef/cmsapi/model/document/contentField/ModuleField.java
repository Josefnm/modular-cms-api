package se.josef.cmsapi.model.document.contentField;


import lombok.EqualsAndHashCode;
import se.josef.cmsapi.model.document.Content;

@EqualsAndHashCode(callSuper = true)
public class ModuleField extends ContentField<Content> {

    public ModuleField() {
    }

    public ModuleField(String name, Content data) {
        super(name, data);
    }
}
