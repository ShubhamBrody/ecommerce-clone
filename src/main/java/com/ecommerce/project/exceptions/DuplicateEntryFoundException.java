package com.ecommerce.project.exceptions;

public class DuplicateEntryFoundException extends RuntimeException {
    String resourceName;
    String field;
    String fieldName;
    String fieldIdName;
    Long fieldId;
    public DuplicateEntryFoundException(String resourceName, String field, String fieldName, String fieldIdName, Long fieldId) {
        super(String.format("%s with %s: %s was already present with %s: : %d",
                resourceName, field,  fieldName, fieldIdName, fieldId));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }

    public DuplicateEntryFoundException() {}
}
