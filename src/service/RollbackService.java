package service;

import java.math.BigDecimal;

import dao.TransactionDAO;
import enums.AuditEventType;
import enums.TransactionStatus;
import exception.InsufficientBalanceException;
import exception.InvalidWalletException;
import exception.RollbackException;
import exception.TransactionFailedException;
import exception.WalletBlockedException;
import model.Transaction;

public class RollbackService {

    private final TransactionDAO transactionDAO;
    private final TransactionService transactionService;
    private final AuditService auditService;

    public RollbackService() {
        this.transactionDAO = new TransactionDAO();
        this.transactionService = new TransactionService();
        this.auditService = new AuditService();
    }

    public Transaction rollbackTransaction(String originalTransactionId)
            throws RollbackException, InvalidWalletException, WalletBlockedException,
                   InsufficientBalanceException, TransactionFailedException {

        if (originalTransactionId == null || originalTransactionId.trim().isEmpty()) {
            throw new RollbackException("Original transaction ID cannot be empty.");
        }

        // Log rollback initiated
        auditService.logEvent(
                originalTransactionId,
                AuditEventType.ROLLBACK_INITIATED,
                "Rollback initiated for transaction: " + originalTransactionId
        );

        // Fetch original transaction
        Transaction originalTransaction = transactionDAO.getTransactionById(originalTransactionId);

        if (originalTransaction == null) {
            auditService.logEvent(
                    originalTransactionId,
                    AuditEventType.ROLLBACK_FAILED,
                    "Rollback failed because original transaction was not found."
            );

            throw new RollbackException("Original transaction not found: " + originalTransactionId);
        }

        // Allow rollback only for SUCCESS transactions
        if (originalTransaction.getStatus() != TransactionStatus.SUCCESS) {
            auditService.logEvent(
                    originalTransactionId,
                    AuditEventType.ROLLBACK_FAILED,
                    "Rollback failed because transaction status is not SUCCESS."
            );

            throw new RollbackException("Only SUCCESS transactions can be rolled back.");
        }

        // Placeholder duplicate rollback prevention
        if (transactionDAO.isRollbackAlreadyDone(originalTransactionId)) {
            auditService.logEvent(
                    originalTransactionId,
                    AuditEventType.ROLLBACK_FAILED,
                    "Rollback failed because it has already been performed once."
            );

            throw new RollbackException("Rollback already performed for transaction: " + originalTransactionId);
        }

        String originalSourceWalletId = originalTransaction.getSourceWalletId();
        String originalDestinationWalletId = originalTransaction.getDestinationWalletId();
        BigDecimal amount = originalTransaction.getAmount();

        // Reverse transfer: destination -> source
        Transaction rollbackTransaction = transactionService.transferFunds(
                originalDestinationWalletId,
                originalSourceWalletId,
                amount
        );

        if (rollbackTransaction == null) {
            auditService.logEvent(
                    originalTransactionId,
                    AuditEventType.ROLLBACK_FAILED,
                    "Rollback failed during reverse transfer."
            );

            throw new RollbackException("Rollback failed for transaction: " + originalTransactionId);
        }

        // Optional hook for future DB schema enhancement
        transactionDAO.markTransactionAsRollbackReference(
                rollbackTransaction.getTransactionId(),
                originalTransactionId
        );

        // Mark original transaction status as rolled back
        transactionDAO.updateTransactionStatus(originalTransactionId, TransactionStatus.ROLLED_BACK);

        // Success logs
        auditService.logEvent(
                originalTransactionId,
                AuditEventType.ROLLBACK,
                "Rollback processed successfully for original transaction."
        );

        auditService.logEvent(
                originalTransactionId,
                AuditEventType.ROLLBACK_SUCCESS,
                "Rollback completed successfully. Reverse TXN: " + rollbackTransaction.getTransactionId()
        );

        return rollbackTransaction;
    }
}