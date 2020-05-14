package se.josef.cmsapi.model.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import se.josef.cmsapi.model.document.contentField.ContentField;

import java.util.Date;
import java.util.List;

/**
 * Content created by a user. Content is created from a template that models which contentFields it can contain.
 * Content belongs to a specific project.
 **/
@Document
@Data
@AllArgsConstructor
@Builder
public class Content {

    private String id;
    @Indexed
    @CreatedBy
    private String ownerId;
    @Indexed
    private String projectId;
    @Indexed
    private String templateId;
    @Indexed
    @CreatedDate
    private Date created;
    @Indexed
    @LastModifiedDate
    private Date updated;
    @Indexed
    private String name;
    private Boolean isPublic;
    @Indexed
    private List<ContentField<?>> contentFields;

    //only used when returning name joined from user document, not persisted
    private String ownerName;
    private String templateName;
}
