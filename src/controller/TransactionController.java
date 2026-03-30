package controller;

import java.math.BigDecimal;
import java.util.List;

import dao.TransactionDAO;
import exception.InsufficientBalanceException;
import exception.InvalidWalletException;
import exception.TransactionFailedException;
import exception.WalletBlockedException;
import model.Transaction;
import service.TransactionService;

public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionDAO transactionDAO;

    public TransactionController() {
        this.transactionService = new TransactionService();
        this.transactionDAO = new TransactionDAO();
    }

    public Transaction transferFunds(String sourceWalletId, String destinationWalletId, BigDecimal amount)
            throws InvalidWalletException, WalletBlockedException,
                   InsufficientBalanceException, TransactionFailedException {
        return transactionService.transferFunds(sourceWalletId, destinationWalletId, amount);
    }

    public Transaction getTransactionById(String transactionId) {
        return transactionDAO.getTransactionById(transactionId);
    }

    public List<Transaction> getAllTransactions() {
        return transactionDAO.getAllTransactions();
    }

    public List<Transaction> getTransactionsByWalletId(String walletId) {
        return transactionDAO.getTransactionsByWalletId(walletId);
    }
}