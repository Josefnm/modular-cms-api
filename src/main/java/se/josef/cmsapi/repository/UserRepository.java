package se.josef.cmsapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import se.josef.cmsapi.model.document.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {


}
