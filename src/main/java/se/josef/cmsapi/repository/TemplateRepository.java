package se.josef.cmsapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import se.josef.cmsapi.model.document.Template;

import java.util.List;

@Repository
public interface TemplateRepository extends MongoRepository<Template, String> {

    List<Template> findByProjectIdOrderByCreatedDesc(String projectId);
    void deleteByProjectId(String projectId);
}
