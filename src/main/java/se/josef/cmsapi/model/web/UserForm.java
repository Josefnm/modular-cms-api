package se.josef.cmsapi.model.web;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserForm {
    private String userName;
    private String email;
    private String password;
}
