package util;

import java.math.BigDecimal;

public class ValidationUtil {
	private ValidationUtil() {
        // Prevent instantiation
    }

    public static boolean isValidOwnerName(String ownerName) {
        return ownerName != null && !ownerName.trim().isEmpty() && ownerName.trim().length() >= 2;
    }

    public static boolean isValidWalletId(String walletId) {
        return walletId != null && !walletId.trim().isEmpty() && walletId.startsWith("WALLET-");
    }

    public static boolean isValidAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
}