package se.josef.cmsapi.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public abstract class AbstractCustomImpl {
    final MongoTemplate mongoTemplate;

    public AbstractCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    SortOperation byCreatedDescSortOp() {
        return sort(Sort.Direction.DESC, "created");
    }

    /**
     * joins user document to a document with matching ownerId
     */
    LookupOperation userLookupOp() {
        return lookup("user", "ownerId", "_id", "owner");
    }

    /**
     * Projects the properties of the base class and name of the user,
     * excluding the other user properties from the result
     * @param tClass base entity of the query
     */
    ProjectionOperation ownerNameProjectionOp(Class<?> tClass) {
        return project(tClass).andExpression("owner.name").as("ownerName");
    }

    /**
     * returns result of the aggregation query
     * @param aggregation the search query
     * @param outputType class of the aggregation result and name of the collection
     * @param <T> Type of returned documents
     * @return entities matching query
     */
    <T> List<T> getResultForQuery(Aggregation aggregation, Class<T> outputType) {
        AggregationResults<T> res = mongoTemplate.aggregate(aggregation, outputType.getSimpleName().toLowerCase(), outputType);

        return res.getMappedResults();
    }
}
