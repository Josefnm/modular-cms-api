package se.josef.cmsapi.model.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import se.josef.cmsapi.model.document.ContentField;

import java.util.List;

@Document
@Data
@AllArgsConstructor
@Builder
public class ContentForm {
    private String projectId;
    private String templateId;
    private String name;
    private String description;
    private List<ContentField<?>> contentFields;
}
