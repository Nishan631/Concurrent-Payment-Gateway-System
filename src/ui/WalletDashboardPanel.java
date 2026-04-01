package ui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import controller.WalletController;
import model.Wallet;

public class WalletDashboardPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton refreshButton;

    private final WalletController walletController;

    public WalletDashboardPanel() {
        this.walletController = new WalletController();
        initializeUI();
        loadWallets();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(
                new Object[]{"Wallet ID", "Owner Name", "Balance", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshButton = new JButton("Refresh Wallets");
        refreshButton.addActionListener(e -> loadWallets());
        add(refreshButton, BorderLayout.SOUTH);
    }

    public void loadWallets() {
        tableModel.setRowCount(0);

        List<Wallet> wallets = walletController.getAllWallets();

        for (Wallet wallet : wallets) {
            tableModel.addRow(new Object[]{
                    wallet.getWalletId(),
                    wallet.getOwnerName(),
                    wallet.getBalance(),
                    wallet.getStatus()
            });
        }
    }
}