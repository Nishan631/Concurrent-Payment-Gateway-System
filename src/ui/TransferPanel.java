package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.concurrent.Future;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import engine.TransactionEngine;
import model.Transaction;

public class TransferPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

    private JTextField sourceWalletField;
    private JTextField destinationWalletField;
    private JTextField amountField;
    private JButton transferButton;

    private final TransactionEngine transactionEngine;
    private final Runnable refreshAllCallback;

    public TransferPanel(Runnable refreshAllCallback) {
        this.transactionEngine = new TransactionEngine();
        this.refreshAllCallback = refreshAllCallback;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Transfer Funds"));

        formPanel.add(new JLabel("Source Wallet ID:"));
        sourceWalletField = new JTextField();
        formPanel.add(sourceWalletField);

        formPanel.add(new JLabel("Destination Wallet ID:"));
        destinationWalletField = new JTextField();
        formPanel.add(destinationWalletField);

        formPanel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        formPanel.add(amountField);

        transferButton = new JButton("Transfer");
        formPanel.add(new JLabel());
        formPanel.add(transferButton);

        add(formPanel, BorderLayout.NORTH);

        transferButton.addActionListener(e -> transferFunds());
    }

    private void transferFunds() {
        try {
            String sourceWalletId = sourceWalletField.getText().trim();
            String destinationWalletId = destinationWalletField.getText().trim();
            BigDecimal amount = new BigDecimal(amountField.getText().trim());

            transferButton.setEnabled(false);

            Future<Transaction> future = transactionEngine.submitTransaction(sourceWalletId, destinationWalletId, amount);

            new Thread(() -> {
                try {
                    Transaction transaction = future.get();

                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                                this,
                                "Transfer successful!\nTransaction ID: " + transaction.getTransactionId(),
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        sourceWalletField.setText("");
                        destinationWalletField.setText("");
                        amountField.setText("");

                        if (refreshAllCallback != null) {
                            refreshAllCallback.run();
                        }

                        transferButton.setEnabled(true);
                    });

                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        String message = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();

                        JOptionPane.showMessageDialog(
                                this,
                                message,
                                "Transfer Failed",
                                JOptionPane.ERROR_MESSAGE
                        );

                        transferButton.setEnabled(true);
                    });
                }
            }).start();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid transfer amount.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}