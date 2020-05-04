package se.josef.cmsapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import se.josef.cmsapi.model.document.Project;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {

    List<Project> findAllByMemberIdsOrOwnerIdOrderByCreatedDesc(String memberId,String ownerId);

    List<Project> findAllByOwnerIdOrderByCreatedDesc(String ownerId);

    Optional<Project> findByIdAndMemberIds(String id, String memberId);

    Optional<Project> deleteByIdAndOwnerId(String id, String ownerId);
}