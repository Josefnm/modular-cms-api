package se.josef.cmsapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import se.josef.cmsapi.model.document.Content;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepository extends MongoRepository<Content, String>, ContentRepositoryCustom {

    Optional<Content> findByIdAndIsPublicTrue(String id);

    List<Content> findByProjectIdOrderByCreatedDesc(String userId);

    List<Content> findByOwnerIdOrderByCreatedDesc(String userId);

    List<Content> findByIsPublicTrue();

    Long deleteByProjectId(String projectId);
}
