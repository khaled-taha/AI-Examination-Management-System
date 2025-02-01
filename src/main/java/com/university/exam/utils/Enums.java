package com.university.exam.utils;

public class Enums {
    public enum ApiStatus {
        NOT_FOUND("Not Found", "The requested resource was not found."),
        BAD_REQUEST("Bad Request", "The request was invalid or malformed."),
        INTERNAL_SERVER_ERROR("Internal Server Error", "An unexpected error occurred on the server.");

        private final String status;
        private final String description;

        ApiStatus(String status, String description) {
            this.status = status;
            this.description = description;
        }

        public String getStatus() {
            return status;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return status + ": " + description;
        }
    }
}
