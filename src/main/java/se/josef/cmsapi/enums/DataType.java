package se.josef.cmsapi.enums;

import se.josef.cmsapi.model.document.contentField.*;

/**
 * Datatypes that can be saved by the cms.
 */
public enum DataType {
    STRING(StringField.class),
    FORMATTED_TEXT(TextField.class),
    IMAGE(ImageField.class), //address to image
    NUMBER(NumberField.class),
    DATE(DateField.class),
    BOOL(BooleanField.class),
    CONTENT(ModuleField.class); //reference to other template
    //TODO add more types

    private Class<?> type;

    public Class<?> getType() {
        return this.type;
    }

    DataType(Class<?> type) {
        this.type = type;
    }
}
