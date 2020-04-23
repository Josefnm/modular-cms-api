package se.josef.cmsapi.model.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import se.josef.cmsapi.enums.DataType;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ContentField<T extends Serializable> {

    private String name;
    private T data;
    private DataType dataType;

}
