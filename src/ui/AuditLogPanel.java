package ui;

import java.awt.BorderLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import dao.AuditLogDAO;
import model.AuditLog;

public class AuditLogPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private final AuditLogDAO auditLogDAO;

    public AuditLogPanel() {
        this.auditLogDAO = new AuditLogDAO();
        initializeUI();
        loadAuditLogs();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(
                new Object[]{"Log ID", "Reference ID", "Event Type", "Message", "Created At"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshButton = new JButton("Refresh Audit Logs");
        refreshButton.addActionListener(e -> loadAuditLogs());
        add(refreshButton, BorderLayout.SOUTH);
    }

    private void loadAuditLogs() {
        tableModel.setRowCount(0);

        List<AuditLog> logs = auditLogDAO.getAllAuditLogs();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (AuditLog log : logs) {
            tableModel.addRow(new Object[]{
                    log.getLogId(),
                    log.getReferenceId(),
                    log.getEventType(),
                    log.getMessage(),
                    log.getCreatedAt() != null ? log.getCreatedAt().format(formatter) : ""
            });
        }
    }
}
