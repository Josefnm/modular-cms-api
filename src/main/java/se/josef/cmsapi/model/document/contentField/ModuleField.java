package se.josef.cmsapi.model.document.contentField;


import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ModuleField extends ContentField<String> {
    public ModuleField(String name, String data) {
        super(name, data);
    }
}
