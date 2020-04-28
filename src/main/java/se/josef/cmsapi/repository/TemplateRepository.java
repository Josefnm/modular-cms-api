package se.josef.cmsapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import se.josef.cmsapi.model.document.Content;
import se.josef.cmsapi.model.document.Template;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateRepository extends MongoRepository<Template, String> {

    Optional<Template> findByIdAndOwnerIdOrIsPublic(String id, String ownerId, Boolean isPublic);

    List<Template> findByOwnerIdOrderByCreatedDesc(String userId);
}
