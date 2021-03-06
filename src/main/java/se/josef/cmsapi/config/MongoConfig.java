package se.josef.cmsapi.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
@EnableMongoAuditing
public class MongoConfig {
    private final MongoTemplate mongoTemplate;
    private final MongoConverter mongoConverter;

    public MongoConfig(MongoTemplate mongoTemplate, MongoConverter mongoConverter) {
        this.mongoTemplate = mongoTemplate;
        this.mongoConverter = mongoConverter;
    }

    /**
     * Sets up mongo db indexing after startup
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initIndicesAfterStartup() {

        var mappingContext = this.mongoConverter.getMappingContext();

        if (!(mappingContext instanceof MongoMappingContext)) return;

        var mongoMappingContext = (MongoMappingContext) mappingContext;
        for (var persistentEntity : mongoMappingContext.getPersistentEntities()) {

            var tClass = persistentEntity.getType();
            if (tClass.isAnnotationPresent(Document.class)) {
                var resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);

                var indexOps = mongoTemplate.indexOps(tClass);
                resolver.resolveIndexFor(tClass).forEach(indexOps::ensureIndex);
            }
        }

    }
}
