package se.josef.cmsapi.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import se.josef.cmsapi.exception.TemplateException;
import se.josef.cmsapi.model.document.Template;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Slf4j
public class TemplateRepositoryCustomImpl extends AbstractCustomImpl implements TemplateRepositoryCustom {

    public TemplateRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    /**
     * Returns templates with matching projectId
     * owner name is joined to document via aggregation lookup
     */
    @Override
    public List<Template> findByProjectIdOrderByCreatedDesc(String projectId) {
        return executeQuery(Criteria.where("projectId").is(projectId));
    }


    /**
     * Returns templates with matching projectId and name regex
     * owner name is joined to document via aggregation lookup
     */
    @Override
    public List<Template> findByNameRegexAndProjectIdOrderByCreated(String name, String projectId) {
        return executeQuery(Criteria.where("name").regex(name).and("projectId").is(projectId));
    }

    private List<Template> executeQuery(Criteria criteria) {
        try {
            var aggregation = newAggregation(
                    match(criteria),
                    byCreatedDescSortOp(),
                    userLookupOp(),
                    ownerNameProjectionOp(Template.class)
            );
            return getResultForQuery(aggregation, Template.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new TemplateException("Database error");
        }
    }

}
