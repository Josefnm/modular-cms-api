package se.josef.cmsapi.service;

import org.springframework.stereotype.Service;
import se.josef.cmsapi.model.document.Content;
import se.josef.cmsapi.repository.ContentRepository;

import java.util.List;

@Service
public class ContentService {

    private final ContentRepository contentRepository;

    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    public Content saveContent(Content content) {
        return contentRepository.save(content);
    }

    public List<Content> getAllContent() {
        return contentRepository.findAll();
    }
}
