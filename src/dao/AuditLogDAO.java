package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import enums.AuditEventType;
import model.AuditLog;

public class AuditLogDAO {
	public boolean insertAuditLog(AuditLog auditLog) {
        String sql = "INSERT INTO audit_logs (reference_id, event_type, message, created_at) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, auditLog.getReferenceId());
            ps.setString(2, auditLog.getEventType().name());
            ps.setString(3, auditLog.getMessage());
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(auditLog.getCreatedAt()));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<AuditLog> getAllAuditLogs() {
        String sql = "SELECT log_id, reference_id, event_type, message, created_at FROM audit_logs ORDER BY created_at DESC";
        List<AuditLog> logs = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AuditLog log = new AuditLog();
                log.setLogId(rs.getInt("log_id"));
                log.setReferenceId(rs.getString("reference_id"));
                log.setEventType(AuditEventType.valueOf(rs.getString("event_type")));
                log.setMessage(rs.getString("message"));

                java.sql.Timestamp timestamp = rs.getTimestamp("created_at");
                if (timestamp != null) {
                    log.setCreatedAt(timestamp.toLocalDateTime());
                }

                logs.add(log);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }

    public List<AuditLog> getAuditLogsByReferenceId(String referenceId) {
        String sql = "SELECT log_id, reference_id, event_type, message, created_at " +
                     "FROM audit_logs WHERE reference_id = ? ORDER BY created_at DESC";

        List<AuditLog> logs = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, referenceId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuditLog log = new AuditLog();
                    log.setLogId(rs.getInt("log_id"));
                    log.setReferenceId(rs.getString("reference_id"));
                    log.setEventType(AuditEventType.valueOf(rs.getString("event_type")));
                    log.setMessage(rs.getString("message"));

                    java.sql.Timestamp timestamp = rs.getTimestamp("created_at");
                    if (timestamp != null) {
                        log.setCreatedAt(timestamp.toLocalDateTime());
                    }

                    logs.add(log);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }
}