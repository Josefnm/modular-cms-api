package se.josef.cmsapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import se.josef.cmsapi.model.document.Template;

@Repository
public interface TemplateRepository extends MongoRepository<Template, String>, TemplateRepositoryCustom {

    void deleteByProjectId(String projectId);

}
