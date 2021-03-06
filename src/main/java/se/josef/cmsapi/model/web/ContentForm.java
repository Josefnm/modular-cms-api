package se.josef.cmsapi.model.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import se.josef.cmsapi.model.document.contentField.ContentField;

import java.util.List;

/**
 * Form for saving content
 */
@AllArgsConstructor
@Data
public class ContentForm {
    private String projectId;
    private String templateId;
    private String name;
    private boolean isPublic;
    private List<ContentField<?>> contentFields;
}
