package se.josef.cmsapi.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import se.josef.cmsapi.exception.TemplateException;
import se.josef.cmsapi.model.document.Template;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Slf4j
public class TemplateRepositoryCustomImpl implements TemplateRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public TemplateRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Finds templates matching projectId
     * Joins owners user name to the entity
     *
     * @param projectId of the project they belong to
     * @return matching templates
     */
    @Override
    public List<Template> findByProjectIdOrderByCreatedDesc(String projectId) {
        return executeQuery(Criteria.where("projectId").is(projectId));
    }

    /**
     * Finds templates matching parameters
     * Joins owners user name to the entity
     *
     * @param name      regex string
     * @param projectId of the project the template belongs to
     * @return matching templates
     */
    @Override
    public List<Template> findByNameRegexAndProjectIdOrderByCreated(String name, String projectId) {
        return executeQuery(Criteria.where("name").regex(name).and("projectId").is(projectId));
    }

    /**
     * Aggregation query that finds matching templates and joins author name to them
     *
     * @param criteria
     * @return matching templates
     */
    private List<Template> executeQuery(Criteria criteria) {
        try {
            var aggregation = newAggregation(
                    match(criteria),
                    sort(Sort.Direction.DESC, "created"),
                    lookup("user", "ownerId", "_id", "owner"),
                    project(Template.class).andExpression("owner.name").as("ownerName")
            );
            var aggregationResults = mongoTemplate.aggregate(aggregation, Template.class.getSimpleName().toLowerCase(), Template.class);

            return aggregationResults.getMappedResults();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new TemplateException("Database error");
        }
    }

}
