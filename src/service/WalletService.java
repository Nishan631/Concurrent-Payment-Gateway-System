package service;

import java.math.BigDecimal;
import java.util.List;

import dao.WalletDAO;
import enums.AuditEventType;
import enums.WalletStatus;
import exception.InvalidWalletException;
import model.Wallet;
import util.IdGenerator;
import util.ValidationUtil;

public class WalletService {
	private final WalletDAO walletDAO;
    private final AuditService auditService;

    public WalletService() {
        this.walletDAO = new WalletDAO();
        this.auditService = new AuditService();
    }

    public Wallet createWallet(String ownerName, BigDecimal initialBalance) throws InvalidWalletException {

        if (!ValidationUtil.isValidOwnerName(ownerName)) {
            throw new InvalidWalletException("Owner name is invalid. It must be at least 2 characters.");
        }

        if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidWalletException("Initial balance cannot be negative.");
        }

        String walletId = IdGenerator.generateWalletId();

        // Ensure generated ID is unique (important if test data already exists)
        while (walletDAO.walletExists(walletId)) {
            walletId = IdGenerator.generateWalletId();
        }

        Wallet wallet = new Wallet(walletId, ownerName.trim(), initialBalance, WalletStatus.ACTIVE);

        boolean created = walletDAO.createWallet(wallet);

        if (!created) {
            throw new InvalidWalletException("Failed to create wallet in database.");
        }

        auditService.logEvent(
                walletId,
                AuditEventType.WALLET_CREATED,
                "Wallet created for " + ownerName.trim() + " with initial balance " + initialBalance
        );

        return wallet;
    }

    public Wallet getWalletById(String walletId) throws InvalidWalletException {
        if (!ValidationUtil.isValidWalletId(walletId)) {
            throw new InvalidWalletException("Wallet ID is invalid.");
        }

        Wallet wallet = walletDAO.getWalletById(walletId);

        if (wallet == null) {
            throw new InvalidWalletException("Wallet not found for ID: " + walletId);
        }

        return wallet;
    }

    public List<Wallet> getAllWallets() {
        return walletDAO.getAllWallets();
    }

    public boolean blockWallet(String walletId) throws InvalidWalletException {
        Wallet wallet = getWalletById(walletId);

        if (wallet.getStatus() == WalletStatus.BLOCKED) {
            return true; // Already blocked
        }

        boolean updated = walletDAO.updateWalletStatus(walletId, WalletStatus.BLOCKED);

        if (updated) {
            auditService.logEvent(
                    walletId,
                    AuditEventType.WALLET_BLOCKED,
                    "Wallet has been blocked."
            );
        }

        return updated;
    }

    public boolean activateWallet(String walletId) throws InvalidWalletException {
        Wallet wallet = getWalletById(walletId);

        if (wallet.getStatus() == WalletStatus.ACTIVE) {
            return true; // Already active
        }

        boolean updated = walletDAO.updateWalletStatus(walletId, WalletStatus.ACTIVE);

        if (updated) {
            auditService.logEvent(
                    walletId,
                    AuditEventType.WALLET_ACTIVATED,
                    "Wallet has been re-activated."
            );
        }

        return updated;
    }

    public boolean updateWalletStatus(String walletId, WalletStatus status) throws InvalidWalletException {
        getWalletById(walletId); // validate existence

        boolean updated = walletDAO.updateWalletStatus(walletId, status);

        if (updated) {
            AuditEventType eventType;

            switch (status) {
                case ACTIVE:
                    eventType = AuditEventType.WALLET_ACTIVATED;
                    break;
                case BLOCKED:
                    eventType = AuditEventType.WALLET_BLOCKED;
                    break;
                default:
                    eventType = AuditEventType.WALLET_BLOCKED; // fallback
                    break;
            }

            auditService.logEvent(
                    walletId,
                    eventType,
                    "Wallet status updated to: " + status
            );
        }

        return updated;
    }
}