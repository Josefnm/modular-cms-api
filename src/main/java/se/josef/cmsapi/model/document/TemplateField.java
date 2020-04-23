package se.josef.cmsapi.model.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import se.josef.cmsapi.enums.DataType;

@Data
@AllArgsConstructor
public class TemplateField {
    private String name;
    private DataType dataType;
}
