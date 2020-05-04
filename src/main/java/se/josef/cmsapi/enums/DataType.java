package se.josef.cmsapi.enums;

import java.util.Date;

/**
 * Datatypes that can be saved by the cms.
 */
public enum DataType {
    STRING(String.class),
    FORMATTED_TEXT(String.class),
    IMAGE(String.class), //address to image
    NUMBER(Long.class),
    DATE(Date.class),
    BOOL(Boolean.class),
    TEMPLATE(String.class); //reference to other template
    //TODO add more types

    private Class<?> type;

    public Class<?> getType() {
        return this.type;
    }

    DataType(Class<?> type) {
        this.type = type;
    }
}
