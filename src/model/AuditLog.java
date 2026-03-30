package model;

import java.time.LocalDateTime;
import enums.AuditEventType;

public class AuditLog {
	private int logId;
    private String referenceId; // walletId or transactionId
    private AuditEventType eventType;
    private String message;
    private LocalDateTime createdAt;

    public AuditLog() {
    }

    public AuditLog(int logId, String referenceId, AuditEventType eventType, String message, LocalDateTime createdAt) {
        this.logId = logId;
        this.referenceId = referenceId;
        this.eventType = eventType;
        this.message = message;
        this.createdAt = createdAt;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public AuditEventType getEventType() {
        return eventType;
    }

    public void setEventType(AuditEventType eventType) {
        this.eventType = eventType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "logId=" + logId +
                ", referenceId='" + referenceId + '\'' +
                ", eventType=" + eventType +
                ", message='" + message + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}