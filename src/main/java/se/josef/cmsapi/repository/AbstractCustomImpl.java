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

    LookupOperation userLookupOp() {
        return lookup("user", "ownerId", "_id", "owner");
    }

    ProjectionOperation ownerNameProjectionOp(Class<?> tClass) {
        return project(tClass).andExpression("owner.userName").as("ownerName");
    }

    <T> List<T> getResultForQuery(Aggregation aggregation, Class<T> outputType) {
        AggregationResults<T> res = mongoTemplate.aggregate(aggregation, outputType.getSimpleName().toLowerCase(), outputType);

        return res.getMappedResults();
    }
}
