package com.web.movierater.models.dtos;

import java.time.OffsetDateTime;
import java.util.List;

public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;
    private OffsetDateTime timestamp;
    private List<FieldError> fieldErrors;

    public ErrorResponse() {}

    public ErrorResponse(int status, String error, String message,
                         String path, OffsetDateTime timestamp,
                         List<FieldError> fieldErrors) {
        this.setStatus(status);
        this.setError(error);
        this.setMessage(message);
        this.setPath(path);
        this.setTimestamp(timestamp);
        this.setFieldErrors(fieldErrors);
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }
    public void setFieldErrors(List<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }


    public static class FieldError {
        private String field;
        private String rejectedValue;
        private String message;

        public FieldError(String field, String rejectedValue, String message) {
            this.setField(field);
            this.setRejectedValue(rejectedValue);
            this.setMessage(message);
        }

        public String getField() {
            return field;
        }
        public void setField(String field) {
            this.field = field;
        }

        public String getRejectedValue() {
            return rejectedValue;
        }
        public void setRejectedValue(String rejectedValue) {
            this.rejectedValue = rejectedValue;
        }

        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
    }
}
