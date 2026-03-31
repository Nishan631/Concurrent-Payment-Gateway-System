package util;

import java.math.BigDecimal;

public class Constants {

    private Constants() {
        // Prevent instantiation
    }

    // Database property keys
    public static final String DB_URL_KEY = "db.url";
    public static final String DB_USERNAME_KEY = "db.username";
    public static final String DB_PASSWORD_KEY = "db.password";
    public static final String DB_DRIVER_KEY = "db.driver";

    // ID prefixes (for reference only)
    public static final String WALLET_ID_PREFIX = "WALLET-";
    public static final String TRANSACTION_ID_PREFIX = "TXN-";

    // Business rules
    public static final BigDecimal MIN_INITIAL_BALANCE = BigDecimal.ZERO;
    public static final BigDecimal MIN_TRANSFER_AMOUNT = new BigDecimal("0.01");

    // Messages
    public static final String WALLET_CREATED_SUCCESS = "Wallet created successfully.";
    public static final String TRANSACTION_SUCCESS = "Transaction completed successfully.";
    public static final String ROLLBACK_SUCCESS = "Transaction rollback completed successfully.";
}