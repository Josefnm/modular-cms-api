package se.josef.cmsapi.model.web;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Form for registering new users
 */
@Data
@AllArgsConstructor
public class UserForm {
    private String name;
    private String email;
    private String password;
}
