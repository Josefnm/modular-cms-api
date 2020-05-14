package se.josef.cmsapi.model.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;


/**
 * A model for a type of content. Belongs to a specific project
 */
@Document
@Data
@AllArgsConstructor
@Builder
public class Template {

    private String id;
    @Indexed
    @CreatedBy
    private String ownerId;
    @Indexed
    private String projectId;
    @CreatedDate
    private Date created;
    @LastModifiedDate
    private Date updated;
    @Indexed
    private String name;
    private String description;
    private List<TemplateField> templateFields;
    //only used when returning name joined from user document, not stored in database
    private String ownerName;
}
