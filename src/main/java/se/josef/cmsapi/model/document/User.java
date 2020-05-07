package se.josef.cmsapi.model.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
@AllArgsConstructor
@Builder
public class User {

    private String id;
    // TextIndex makes text search queries faster
    @TextIndexed
    private String userName;
    @TextIndexed
    private String email;
    @CreatedDate
    private Date created;
    @LastModifiedDate
    private Date updated;
}

