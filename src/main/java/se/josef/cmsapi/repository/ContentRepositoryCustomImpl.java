package se.josef.cmsapi.repository;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import se.josef.cmsapi.exception.ContentException;
import se.josef.cmsapi.model.document.Content;
import se.josef.cmsapi.model.web.contentsearch.ContentSearch;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.ConvertOperators.ToObjectId.toObjectId;

@Slf4j
public class ContentRepositoryCustomImpl extends AbstractCustomImpl implements ContentRepositoryCustom {


    public ContentRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }


    /**
     * Returns content with matching projectId
     * owner name is joined to document via aggregation lookup
     */
    @Override
    public List<Content> findByProjectIdOrderByCreatedDesc(String projectId) {
        return executeQuery(Criteria.where("projectId").is(projectId));
    }

    /**
     * Search for documents match searchFields criteria and projectId
     */
    @Override
    public List<Content> findByProjectIdAndContentFields(List<ContentSearch<?>> searchFields, String projectId) {
        var criteria = Criteria.where("projectId").is(projectId);
        var joinedCriteria = combineCriteria(searchFields, criteria);
        return executeQuery(joinedCriteria);
    }

    /**
     * Search for documents match searchFields criteria that are public
     */
    @Override
    public List<Content> findByIsPublicAndContentFields(List<ContentSearch<?>> searchFields) {
        var criteria = Criteria.where("isPublic").is(true);
        var joinedCriteria = combineCriteria(searchFields, criteria);
        return executeQuery(joinedCriteria);
    }

    /**
     * Creates search criteria from searchFields and combines them with additional criteria
     * using andOperator
     *
     * @param searchFields  source of search criteria
     * @param extraCriteria additional criteria
     * @return combined criteria
     */
    private Criteria combineCriteria(List<ContentSearch<?>> searchFields, Criteria... extraCriteria) {

        var criteria = searchFields.stream()
                .map(ContentSearch::getCriteria)
                .toArray(Criteria[]::new);
        var allCriteria = ArrayUtils.addAll(extraCriteria, criteria);
        return new Criteria().andOperator(allCriteria);
    }

    private List<Content> executeQuery(Criteria criteria) {
        try {
            // Note: correct ordering is important
            var aggregation = newAggregation(
                    match(criteria),
                    byCreatedDescSortOp(),
                    // transforms string foreign keys to objectIds to make lookup work
                    project(Content.class).and(toObjectId("$templateId")).as("templateId"),
                    userLookupOp(),
                    templateLookupOp(),
                    ownerNameProjectionOp(Content.class)
                            .andExpression("template.name").as("templateName")
            );
            return getResultForQuery(aggregation, Content.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ContentException(e.getMessage());
        }
    }

    /**
     * joins template document to content document with matching templateId
     */
    LookupOperation templateLookupOp() {
        return lookup("template", "templateId", "_id", "template");
    }

}
