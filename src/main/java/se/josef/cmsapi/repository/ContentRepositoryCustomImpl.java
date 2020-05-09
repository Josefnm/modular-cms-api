package se.josef.cmsapi.repository;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import se.josef.cmsapi.exception.ContentException;
import se.josef.cmsapi.model.document.Content;
import se.josef.cmsapi.model.web.contentsearch.ContentSearch;
import se.josef.cmsapi.model.web.contentsearch.PublicSearch;

import java.util.List;

@Slf4j
public class ContentRepositoryCustomImpl implements ContentRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public ContentRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Content> searchByProjectId(List<ContentSearch<?>> searchFields, String projectId) {
        var criteria = Criteria.where("projectId").is(projectId);
        return search(searchFields, criteria);
    }

    @Override
    public List<Content> searchIsPublic(List<ContentSearch<?>> searchFields) {
        searchFields.add(new PublicSearch(true));
        return search(searchFields);
    }

    private List<Content> search(List<ContentSearch<?>> searchFields, Criteria... extraCriteria) {
        try {
            var criteria = searchFields.stream()
                    .map(ContentSearch::getCriteria)
                    .toArray(Criteria[]::new);
            var joinedCriteria = ArrayUtils.addAll(extraCriteria, criteria);

            var query = Query.query(new Criteria().andOperator(joinedCriteria));

            return mongoTemplate.find(query, Content.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ContentException(e.getMessage());
        }

    }

}
