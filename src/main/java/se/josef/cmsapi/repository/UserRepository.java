package se.josef.cmsapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import se.josef.cmsapi.model.document.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom {

    Optional<User> findByEmailOrUserName(String email, String userName);

    List<User> findByIdIn(List<String> id);

}
