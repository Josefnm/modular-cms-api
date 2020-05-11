package se.josef.cmsapi.repository;

import se.josef.cmsapi.model.document.Template;

import java.util.List;

public interface TemplateRepositoryCustom {

    List<Template> findByProjectIdOrderByCreatedDesc(String projectId);

    List<Template> findByNameRegexAndProjectIdOrderByCreated(String name, String projectId);

}
