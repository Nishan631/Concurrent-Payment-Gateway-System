package engine;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

import controller.TransactionController;
import model.Transaction;

public class TransactionTask implements Callable<Transaction> {

    private final TransactionController transactionController;
    private final String sourceWalletId;
    private final String destinationWalletId;
    private final BigDecimal amount;

    public TransactionTask(TransactionController transactionController,
                           String sourceWalletId,
                           String destinationWalletId,
                           BigDecimal amount) {
        this.transactionController = transactionController;
        this.sourceWalletId = sourceWalletId;
        this.destinationWalletId = destinationWalletId;
        this.amount = amount;
    }

    @Override
    public Transaction call() throws Exception {
        return transactionController.transferFunds(sourceWalletId, destinationWalletId, amount);
    }
}