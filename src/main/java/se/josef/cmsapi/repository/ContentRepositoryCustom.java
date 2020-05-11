package se.josef.cmsapi.repository;

import se.josef.cmsapi.model.document.Content;
import se.josef.cmsapi.model.web.contentsearch.ContentSearch;

import java.util.List;

public interface ContentRepositoryCustom {

    List<Content> findByProjectIdAndContentFields(List<ContentSearch<?>> searchFields, String projectId);

    List<Content> findByIsPublicAndContentFields(List<ContentSearch<?>> searchFields);

    List<Content> findByProjectIdOrderByCreatedDesc(String userId);

}
