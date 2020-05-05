package se.josef.cmsapi.model.document.contentField;

import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
public class DateField extends ContentField<Date> {

    public DateField() {
    }

    public DateField(String name, Date data) {
        super(name, data);
    }
}
