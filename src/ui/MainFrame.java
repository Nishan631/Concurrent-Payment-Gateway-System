package ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class MainFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;

    private WalletDashboardPanel walletDashboardPanel;
    private CreateWalletPanel createWalletPanel;
    private TransferPanel transferPanel;
    private TransactionHistoryPanel transactionHistoryPanel;
    private AuditLogPanel auditLogPanel;

    public MainFrame() {
        initializeFrame();
        initializeComponents();
    }

    private void initializeFrame() {
        setTitle("Payment Gateway System");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    private void initializeComponents() {
        walletDashboardPanel = new WalletDashboardPanel();
        transactionHistoryPanel = new TransactionHistoryPanel();
        auditLogPanel = new AuditLogPanel();

        Runnable refreshAllCallback = () -> {
            walletDashboardPanel.loadWallets();
            transactionHistoryPanel.loadTransactions();
            // audit panel can be refreshed manually, but you can auto-refresh if needed
        };

        createWalletPanel = new CreateWalletPanel(() -> walletDashboardPanel.loadWallets());
        transferPanel = new TransferPanel(refreshAllCallback);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Wallet Dashboard", walletDashboardPanel);
        tabbedPane.addTab("Create Wallet", createWalletPanel);
        tabbedPane.addTab("Transfer Funds", transferPanel);
        tabbedPane.addTab("Transaction History", transactionHistoryPanel);
        tabbedPane.addTab("Audit Logs", auditLogPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }
}