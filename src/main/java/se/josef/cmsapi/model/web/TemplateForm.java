package se.josef.cmsapi.model.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import se.josef.cmsapi.model.document.TemplateField;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class TemplateForm {
    private String projectId;
    private String name;
    private String description;
    private List<TemplateField> templateFields;
}
