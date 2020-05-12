package se.josef.cmsapi.model.document;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TemplateField {
    private String name;
    //Allowed datatypes are the names of the contentfield subclasses
    private String dataType;
}
