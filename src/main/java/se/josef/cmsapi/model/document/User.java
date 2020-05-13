package se.josef.cmsapi.model.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Mirrors firebase user info.
 */
@Document
@Data
@AllArgsConstructor
@Builder
public class User {

    private String id;
    @Indexed
    private String name;
    @Indexed
    private String email;
    @CreatedDate
    private Date created;
    @LastModifiedDate
    private Date updated;
}

