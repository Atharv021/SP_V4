package com.examly.springapp.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TicketStatusConverter implements AttributeConverter<TicketStatus, String> {

    @Override
    public String convertToDatabaseColumn(TicketStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public TicketStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return TicketStatus.fromValue(dbData);
    }
}
