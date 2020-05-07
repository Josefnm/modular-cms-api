package se.josef.cmsapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.josef.cmsapi.exception.ProjectException;
import se.josef.cmsapi.model.document.Project;
import se.josef.cmsapi.model.web.ProjectForm;
import se.josef.cmsapi.repository.ContentRepository;
import se.josef.cmsapi.repository.ProjectRepository;
import se.josef.cmsapi.repository.TemplateRepository;
import se.josef.cmsapi.util.UserUtils;

import java.util.*;

@Service
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TemplateRepository templateRepository;
    private final ContentRepository contentRepository;
    private final UserUtils userUtils;

    public ProjectService(ProjectRepository projectRepository, TemplateRepository templateRepository, ContentRepository contentRepository, UserUtils userUtils) {
        this.projectRepository = projectRepository;
        this.templateRepository = templateRepository;
        this.contentRepository = contentRepository;
        this.userUtils = userUtils;
    }

    public Project saveProject(ProjectForm projectForm) {
        var ownerId = userUtils.getUserId();
        var created = new Date();
        Project project = Project.builder()
                .ownerId(ownerId)
                .name(projectForm.getName())
                .description(projectForm.getDescription())
                .memberIds(Collections.singletonList(ownerId))
                .created(created)
                .updated(created)
                .build();
        return projectRepository.save(project);
    }

    public boolean deleteProject(String projectId) {
        Long numDeleted = projectRepository
                .deleteByIdAndOwnerId(projectId, userUtils.getUserId());
        if (numDeleted > 0) {
            templateRepository.deleteByProjectId(projectId);
            contentRepository.deleteByProjectId(projectId);
            return true;
        } else {
            throw new ProjectException(String.format("Can't delete project with id; %s", projectId));
        }

    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }


    public List<Project> getProjectsByUserId() {

        String uid = userUtils.getUserId();
        return projectRepository.findAllByMemberIdsOrOwnerIdOrderByCreatedDesc(uid, uid);

    }

    public Project getProjectById(String id) {
        var userId = userUtils.getUserId();
        return projectRepository
                .findByMemberIdsAndId(userId, id)
                .orElseThrow(() ->
                        new ProjectException(String.format("Project with id %s is unavailable", id))
                );
    }

    public Project updateProject(ProjectForm projectForm){
        try{
        var project = getProjectById(projectForm.getId());
        project.setName(projectForm.getName());
        project.setDescription(projectForm.getDescription());
        project.setMemberIds(projectForm.getMemberIds());
        return projectRepository.save(project);

        }catch (Exception e){
            log.error(e.getMessage());
            throw new ProjectException(e.getMessage());
        }
    }

}
