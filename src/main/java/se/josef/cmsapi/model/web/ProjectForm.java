package se.josef.cmsapi.model.web;


import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Form for saving projects
 */
@Data
@AllArgsConstructor
public class ProjectForm {
    private String name;
    private String description;
}
