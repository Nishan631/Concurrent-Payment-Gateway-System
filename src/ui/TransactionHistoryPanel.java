package ui;

import java.awt.BorderLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import controller.TransactionController;
import model.Transaction;

public class TransactionHistoryPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton refreshButton;

    private final TransactionController transactionController;

    public TransactionHistoryPanel() {
        this.transactionController = new TransactionController();
        initializeUI();
        loadTransactions();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(
                new Object[]{"Transaction ID", "Source Wallet", "Destination Wallet", "Amount", "Status", "Created At"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshButton = new JButton("Refresh Transactions");
        refreshButton.addActionListener(e -> loadTransactions());
        add(refreshButton, BorderLayout.SOUTH);
    }

    public void loadTransactions() {
        tableModel.setRowCount(0);

        List<Transaction> transactions = transactionController.getAllTransactions();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Transaction txn : transactions) {
            tableModel.addRow(new Object[]{
                    txn.getTransactionId(),
                    txn.getSourceWalletId(),
                    txn.getDestinationWalletId(),
                    txn.getAmount(),
                    txn.getStatus(),
                    txn.getCreatedAt() != null ? txn.getCreatedAt().format(formatter) : ""
            });
        }
    }
}
