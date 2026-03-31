package util;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
	private static final AtomicInteger walletCounter = new AtomicInteger(1000);
    private static final AtomicInteger transactionCounter = new AtomicInteger(2000);

    private IdGenerator() {
        // Prevent instantiation
    }

    public static String generateWalletId() {
        return "WALLET-" + walletCounter.incrementAndGet();
    }

    public static String generateTransactionId() {
        return "TXN-" + transactionCounter.incrementAndGet();
    }
}