package se.josef.cmsapi.model.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import se.josef.cmsapi.model.document.contentField.ContentField;

import java.util.List;

/**
 * Form for updating content
 */
@AllArgsConstructor
@Data
public class ContentUpdateForm {
    private String id;
    private String name;
    private boolean isPublic;
    private List<ContentField<?>> contentFields;
}
