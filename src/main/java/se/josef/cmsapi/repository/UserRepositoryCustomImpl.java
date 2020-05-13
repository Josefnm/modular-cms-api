package se.josef.cmsapi.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import se.josef.cmsapi.model.document.User;

import java.util.List;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public UserRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Searches for users by matching email or name
     * @param searchString regex string for search
     * @return matching users
     */
    @Override
    public List<User> searchUsers(String searchString) {
        Criteria criteria = new Criteria()
                .orOperator(
                        Criteria.where("userName").regex(searchString, "i"),
                        Criteria.where("email").regex(searchString, "i")
                );
        Query query = new Query(criteria);
        return mongoTemplate.find(query, User.class);
    }

}
