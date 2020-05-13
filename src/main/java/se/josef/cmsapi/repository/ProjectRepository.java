package se.josef.cmsapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import se.josef.cmsapi.model.document.Project;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {

    List<Project> findAllByMemberIdsOrderByCreatedDesc(String memberId, String ownerId);

    Optional<Project> findByMemberIdsAndId(String memberId, String id);

    Boolean existsByMemberIdsAndId(String memberId, String id);

    Long deleteByIdAndOwnerId(String id, String ownerId);
}