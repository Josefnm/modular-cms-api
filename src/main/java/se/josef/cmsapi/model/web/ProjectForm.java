package se.josef.cmsapi.model.web;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProjectForm {

    private String id;
    private String ownerId;
    private List<String> memberIds;
    private String name;
    private String description;
}
