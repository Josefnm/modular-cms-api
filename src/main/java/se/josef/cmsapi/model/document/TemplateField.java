package se.josef.cmsapi.model.document;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TemplateField {
    private String name;
    private String dataType;
}
