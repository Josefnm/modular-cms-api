package se.josef.cmsapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import se.josef.cmsapi.model.document.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom {

    Optional<User> findByEmailOrUserName(String email, String userName);
    List<User> findByIdIn(List<String> id);

   // @Query("{'$or':[ {'userName':{'$regex': '?0', '$options': 'i'}}, {'email':{'$regex': '?0', '$options': 'i'} } ] }")
   // List<User> search(String searchString);


}
