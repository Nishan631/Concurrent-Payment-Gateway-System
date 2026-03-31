package service;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;

import dao.TransactionDAO;
import dao.WalletDAO;
import enums.AuditEventType;
import enums.TransactionStatus;
import enums.WalletStatus;
import exception.InsufficientBalanceException;
import exception.InvalidWalletException;
import exception.TransactionFailedException;
import exception.WalletBlockedException;
import model.Transaction;
import model.Wallet;
import util.DateTimeUtil;
import util.IdGenerator;
import util.ValidationUtil;

public class TransactionService {

    private final WalletDAO walletDAO;
    private final TransactionDAO transactionDAO;
    private final AuditService auditService;

    public TransactionService() {
        this.walletDAO = new WalletDAO();
        this.transactionDAO = new TransactionDAO();
        this.auditService = new AuditService();
    }

    public Transaction transferFunds(String sourceWalletId, String destinationWalletId, BigDecimal amount)
            throws InvalidWalletException, WalletBlockedException,
                   InsufficientBalanceException, TransactionFailedException {

        // Validate wallet IDs
        if (!ValidationUtil.isValidWalletId(sourceWalletId)) {
            throw new InvalidWalletException("Source wallet ID is invalid.");
        }

        if (!ValidationUtil.isValidWalletId(destinationWalletId)) {
            throw new InvalidWalletException("Destination wallet ID is invalid.");
        }

        if (sourceWalletId.equals(destinationWalletId)) {
            throw new TransactionFailedException("Source and destination wallets cannot be the same.");
        }

        // Validate amount
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionFailedException("Transfer amount must be greater than zero.");
        }

        // Fetch wallets
        Wallet sourceWallet = walletDAO.getWalletById(sourceWalletId);
        Wallet destinationWallet = walletDAO.getWalletById(destinationWalletId);

        if (sourceWallet == null) {
            throw new InvalidWalletException("Source wallet not found: " + sourceWalletId);
        }

        if (destinationWallet == null) {
            throw new InvalidWalletException("Destination wallet not found: " + destinationWalletId);
        }

        // Check wallet status
        if (sourceWallet.getStatus() != WalletStatus.ACTIVE) {
            throw new WalletBlockedException("Source wallet is not active: " + sourceWalletId);
        }

        if (destinationWallet.getStatus() != WalletStatus.ACTIVE) {
            throw new WalletBlockedException("Destination wallet is not active: " + destinationWalletId);
        }

        // Log transfer initiated
        auditService.logEvent(
                sourceWalletId,
                AuditEventType.TRANSFER_INITIATED,
                "Transfer initiated from " + sourceWalletId + " to " + destinationWalletId + " for amount " + amount
        );

        // Avoid deadlock by locking in consistent order
        Wallet firstLockWallet = sourceWalletId.compareTo(destinationWalletId) < 0 ? sourceWallet : destinationWallet;
        Wallet secondLockWallet = sourceWalletId.compareTo(destinationWalletId) < 0 ? destinationWallet : sourceWallet;

        Lock firstLock = firstLockWallet.getLock();
        Lock secondLock = secondLockWallet.getLock();

        firstLock.lock();
        secondLock.lock();

        try {
            // Re-check balance inside lock
            if (sourceWallet.getBalance().compareTo(amount) < 0) {
                auditService.logEvent(
                        sourceWalletId,
                        AuditEventType.TRANSFER_FAILED,
                        "Transfer failed due to insufficient balance. Requested: " + amount
                );

                throw new InsufficientBalanceException("Insufficient balance in wallet: " + sourceWalletId);
            }

            // Calculate updated balances
            BigDecimal updatedSourceBalance = sourceWallet.getBalance().subtract(amount);
            BigDecimal updatedDestinationBalance = destinationWallet.getBalance().add(amount);

            // Update DB balances
            boolean sourceUpdated = walletDAO.updateWalletBalance(sourceWalletId, updatedSourceBalance);
            boolean destinationUpdated = walletDAO.updateWalletBalance(destinationWalletId, updatedDestinationBalance);

            if (!sourceUpdated || !destinationUpdated) {
                auditService.logEvent(
                        sourceWalletId,
                        AuditEventType.TRANSFER_FAILED,
                        "Transfer failed due to database balance update error."
                );

                throw new TransactionFailedException("Failed to update wallet balances.");
            }

            // Update in-memory objects
            sourceWallet.setBalance(updatedSourceBalance);
            destinationWallet.setBalance(updatedDestinationBalance);

            // Update registry
            WalletRegistry.registerWallet(sourceWallet);
            WalletRegistry.registerWallet(destinationWallet);

            // Create transaction
            String transactionId = IdGenerator.generateTransactionId();

            Transaction transaction = new Transaction(
                    transactionId,
                    sourceWalletId,
                    destinationWalletId,
                    amount,
                    TransactionStatus.SUCCESS,
                    DateTimeUtil.getCurrentDateTime()
            );

            boolean inserted = transactionDAO.insertTransaction(transaction);

            if (!inserted) {
                auditService.logEvent(
                        sourceWalletId,
                        AuditEventType.TRANSFER_FAILED,
                        "Transfer failed because transaction record could not be inserted."
                );

                throw new TransactionFailedException("Transaction record insertion failed.");
            }

            // Audit logs
            auditService.logEvent(
                    sourceWalletId,
                    AuditEventType.DEBIT,
                    "Debited " + amount + " from wallet " + sourceWalletId +
                    " to wallet " + destinationWalletId + " [TXN: " + transactionId + "]"
            );

            auditService.logEvent(
                    destinationWalletId,
                    AuditEventType.CREDIT,
                    "Credited " + amount + " to wallet " + destinationWalletId +
                    " from wallet " + sourceWalletId + " [TXN: " + transactionId + "]"
            );

            auditService.logEvent(
                    transactionId,
                    AuditEventType.TRANSACTION_CREATED,
                    "Transaction record created successfully."
            );

            auditService.logEvent(
                    transactionId,
                    AuditEventType.TRANSFER_SUCCESS,
                    "Transfer completed successfully from " + sourceWalletId +
                    " to " + destinationWalletId + " for amount " + amount
            );

            return transaction;

        } finally {
            secondLock.unlock();
            firstLock.unlock();
        }
    }
}