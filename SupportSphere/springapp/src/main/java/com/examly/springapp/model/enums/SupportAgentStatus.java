package com.examly.springapp.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SupportAgentStatus {
    AVAILABLE("Available"),
    UNAVAILABLE("Unavailable"),
    DELETED("Deleted");

    private final String value;

    SupportAgentStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static SupportAgentStatus fromValue(String value) {
        if (value == null) return null;
        for (SupportAgentStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}
