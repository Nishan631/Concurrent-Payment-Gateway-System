package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controller.WalletController;
import model.Wallet;

public class CreateWalletPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

    private JTextField ownerNameField;
    private JTextField initialBalanceField;
    private JButton createButton;

    private final WalletController walletController;
    private final Runnable dashboardRefreshCallback;

    public CreateWalletPanel(Runnable dashboardRefreshCallback) {
        this.walletController = new WalletController();
        this.dashboardRefreshCallback = dashboardRefreshCallback;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Create Wallet"));

        formPanel.add(new JLabel("Owner Name:"));
        ownerNameField = new JTextField();
        formPanel.add(ownerNameField);

        formPanel.add(new JLabel("Initial Balance:"));
        initialBalanceField = new JTextField("0.00");
        formPanel.add(initialBalanceField);

        createButton = new JButton("Create Wallet");
        formPanel.add(new JLabel());
        formPanel.add(createButton);

        add(formPanel, BorderLayout.NORTH);

        createButton.addActionListener(e -> createWallet());
    }

    private void createWallet() {
        try {
            String ownerName = ownerNameField.getText().trim();
            BigDecimal initialBalance = new BigDecimal(initialBalanceField.getText().trim());

            Wallet wallet = walletController.createWallet(ownerName, initialBalance);

            JOptionPane.showMessageDialog(
                    this,
                    "Wallet created successfully!\nWallet ID: " + wallet.getWalletId(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            ownerNameField.setText("");
            initialBalanceField.setText("0.00");

            if (dashboardRefreshCallback != null) {
                dashboardRefreshCallback.run();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid numeric initial balance.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Create Wallet Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}