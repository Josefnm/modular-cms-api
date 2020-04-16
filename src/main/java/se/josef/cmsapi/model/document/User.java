package se.josef.cmsapi.model.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
public class User {

    private String id;
    private String fireId;
    private String userName;
    private String email;
}

