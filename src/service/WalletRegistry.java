package service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import model.Wallet;

public class WalletRegistry {

    private static final Map<String, Wallet> walletMap = new HashMap<>();

    private WalletRegistry() {
        // Prevent instantiation
    }

    public static void registerWallet(Wallet wallet) {
        if (wallet != null && wallet.getWalletId() != null) {
            walletMap.put(wallet.getWalletId(), wallet);
        }
    }

    public static Wallet getWallet(String walletId) {
        return walletMap.get(walletId);
    }

    public static boolean containsWallet(String walletId) {
        return walletMap.containsKey(walletId);
    }

    public static void removeWallet(String walletId) {
        walletMap.remove(walletId);
    }

    public static void clearRegistry() {
        walletMap.clear();
    }

    public static Collection<Wallet> getAllWallets() {
        return walletMap.values();
    }
}