package se.josef.cmsapi.model.web;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Form for updating projects
 */
@Data
@AllArgsConstructor
public class ProjectUpdateForm {
    private String id;
    private List<String> memberIds;
    private String name;
    private String description;
}
