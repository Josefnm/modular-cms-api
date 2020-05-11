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


    @Override
    public List<Content> findByProjectIdAndContentFields(List<ContentSearch<?>> searchFields, String projectId) {
        var criteria = Criteria.where("projectId").is(projectId);
        return search(searchFields, criteria);
    }

    @Override
    public List<Content> findByIsPublicAndContentFields(List<ContentSearch<?>> searchFields) {
        var criteria = Criteria.where("isPublic").is(true);
        return search(searchFields, criteria);
    }

    private List<Content> search(List<ContentSearch<?>> searchFields, Criteria... extraCriteria) {

        var criteria = searchFields.stream()
                .map(ContentSearch::getCriteria)
                .toArray(Criteria[]::new);
        var allCriteria = ArrayUtils.addAll(extraCriteria, criteria);
        var joinedCriteria = new Criteria().andOperator(allCriteria);

        return executeQuery(joinedCriteria);

    }

    /**
     * Returns content with matching projectId
     * owner name is joined to document via aggregation lookup
     */
    @Override
    public List<Content> findByProjectIdOrderByCreatedDesc(String projectId) {
        return executeQuery(Criteria.where("projectId").is(projectId));
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
                    ownerNameProjectionOp(Content.class).andExpression("template.name").as("templateName")
            );
            return getResultForQuery(aggregation, Content.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ContentException(e.getMessage());
        }
    }

    LookupOperation templateLookupOp() {
        return lookup("template", "templateId", "_id", "template");
    }

}
