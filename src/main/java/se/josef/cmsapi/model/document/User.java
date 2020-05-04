package se.josef.cmsapi.model.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
@AllArgsConstructor
public class User {

    private String id;
    private String userName;
    private String email;
    private Date created;
}

