package engine;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import controller.TransactionController;
import model.Transaction;

public class TransactionEngine {

    private final ExecutorService executorService;
    private final TransactionController transactionController;

    public TransactionEngine() {
        this.executorService = Executors.newFixedThreadPool(5);
        this.transactionController = new TransactionController();
    }

    public Future<Transaction> submitTransaction(String sourceWalletId, String destinationWalletId, BigDecimal amount) {
        TransactionTask task = new TransactionTask(transactionController, sourceWalletId, destinationWalletId, amount);
        return executorService.submit(task);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}