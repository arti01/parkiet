package arti.parkiet.domain;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@Introspected
@MappedEntity
public class ImportLog {

    private String status;
    private String message;

    @MappedProperty("created_at")
    private String createdAt;

    // 1. Pusty konstruktor (Kluczowy dla bibliotek!)
    public ImportLog() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}