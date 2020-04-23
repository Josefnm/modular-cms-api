package se.josef.cmsapi.model.document;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Document
@Data
@AllArgsConstructor
public class Content {

    private String id;
    private String ownerId;
    private String templateId;
    private String name;
    private Date created;
    private Date updated;
    private List<ContentField<?>> contentFields;
}
