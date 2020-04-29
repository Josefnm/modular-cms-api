package se.josef.cmsapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import se.josef.cmsapi.model.document.Content;
import se.josef.cmsapi.model.document.Project;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {

    List<Project> findAllByMemberIdsOrderByCreatedDesc(String memberId);

    Optional<Project> findByIdAndMemberIds(String id, String memberId);

    Optional<Project> deleteByIdAndOwnerId(String id,String ownerId);
}