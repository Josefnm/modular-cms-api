package se.josef.cmsapi.model.document;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TemplateField {
    private String name;
    //Allowed dataTypes values are the names of the ContentField subclasses
    private String dataType;
}
