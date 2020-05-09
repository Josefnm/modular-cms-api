package se.josef.cmsapi.model.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import se.josef.cmsapi.model.document.contentField.ContentField;

import java.util.Date;
import java.util.List;

/**
 *
 **/
@Document
@Data
@AllArgsConstructor
@Builder
public class Content {

    private String id;
    @Indexed
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
    private List<ContentField<?>> contentFields;
}
