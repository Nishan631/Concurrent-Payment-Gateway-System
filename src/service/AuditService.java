package service;

import java.time.LocalDateTime;
import java.util.List;

import dao.AuditLogDAO;
import enums.AuditEventType;
import model.AuditLog;

public class AuditService {
	private final AuditLogDAO auditLogDAO;

    public AuditService() {
        this.auditLogDAO = new AuditLogDAO();
    }

    public boolean logEvent(String referenceId, AuditEventType eventType, String message) {
        AuditLog auditLog = new AuditLog(
                0,
                referenceId,
                eventType,
                message,
                LocalDateTime.now()
        );

        return auditLogDAO.insertAuditLog(auditLog);
    }

    public List<AuditLog> getAllAuditLogs() {
        return auditLogDAO.getAllAuditLogs();
    }

    public List<AuditLog> getAuditLogsByReferenceId(String referenceId) {
        return auditLogDAO.getAuditLogsByReferenceId(referenceId);
    }
}