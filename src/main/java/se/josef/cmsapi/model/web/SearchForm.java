package se.josef.cmsapi.model.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.josef.cmsapi.model.web.contentsearch.ContentSearch;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchForm {
    private List<ContentSearch<?>> searchFields;

}
