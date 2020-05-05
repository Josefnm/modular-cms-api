package se.josef.cmsapi.model.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
@Data
@AllArgsConstructor
@Builder
public class Project {

    private String id;
    // creator of project
    @Indexed
    private String ownerId;
    // Users with editor access to project
    @Indexed
    private List<String> memberIds;
    @CreatedDate
    private Date created;
    @LastModifiedDate
    private Date updated;
    private String name;
    private String description;
}
