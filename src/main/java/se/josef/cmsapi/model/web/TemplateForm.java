package se.josef.cmsapi.model.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import se.josef.cmsapi.model.document.TemplateField;

import java.util.List;

/**
 * Form for saving templates
 */
@Data
@AllArgsConstructor
public class TemplateForm {
    private String projectId;
    private String name;
    private String description;
    private List<TemplateField> templateFields;
}
