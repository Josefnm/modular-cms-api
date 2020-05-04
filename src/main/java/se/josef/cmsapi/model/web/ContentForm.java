package se.josef.cmsapi.model.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import se.josef.cmsapi.model.document.ContentField;

import java.util.List;

@AllArgsConstructor
@Data
public class ContentForm {
    private String projectId;
    private String templateId;
    private String name;
    private String description;
    private List<ContentField<?>> contentFields;
}
