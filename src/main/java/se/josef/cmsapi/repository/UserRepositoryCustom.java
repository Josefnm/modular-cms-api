package se.josef.cmsapi.repository;

import se.josef.cmsapi.model.document.User;

import java.util.List;

public interface UserRepositoryCustom {

    List<User> searchUsers(String searchString);

}
