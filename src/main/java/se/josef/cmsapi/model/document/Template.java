package se.josef.cmsapi.model.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
@Data
@AllArgsConstructor
public class Template {

    private String id;
    private String ownerId;
    private Date created;
    private Date updated;
    private String name;
    private String description;
    private Boolean isPublic;
    private List<TemplateField> templateFields;
}
