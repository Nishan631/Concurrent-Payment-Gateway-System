package controller;

import java.math.BigDecimal;
import java.util.List;

import dao.WalletDAO;
import enums.WalletStatus;
import model.Wallet;
import util.IdGenerator;

public class WalletController {

    private final WalletDAO walletDAO;

    public WalletController() {
        this.walletDAO = new WalletDAO();
    }

    public Wallet createWallet(String ownerName, BigDecimal initialBalance) throws IllegalArgumentException {
        if (ownerName == null || ownerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner name cannot be empty.");
        }

        if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }

        String walletId = generateWalletId();

        Wallet wallet = new Wallet(
                walletId,
                ownerName.trim(),
                initialBalance,
                WalletStatus.ACTIVE
        );

        boolean created = walletDAO.createWallet(wallet);

        if (!created) {
            throw new IllegalArgumentException("Failed to create wallet in database.");
        }

        return wallet;
    }

    public Wallet getWalletById(String walletId) {
        if (walletId == null || walletId.trim().isEmpty()) {
            return null;
        }
        return walletDAO.getWalletById(walletId.trim());
    }

    public List<Wallet> getAllWallets() {
        return walletDAO.getAllWallets();
    }

    public boolean updateWalletStatus(String walletId, WalletStatus status) {
        if (walletId == null || walletId.trim().isEmpty() || status == null) {
            return false;
        }
        return walletDAO.updateWalletStatus(walletId.trim(), status);
    }

    public boolean deleteWallet(String walletId) {
        if (walletId == null || walletId.trim().isEmpty()) {
            return false;
        }
        return walletDAO.deleteWallet(walletId.trim());
    }

    private String generateWalletId() {
        return IdGenerator.generateWalletId();
    }
}