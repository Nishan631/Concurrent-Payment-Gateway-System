package model;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;
import enums.WalletStatus;

public class Wallet {
	private String walletId;
    private String ownerName;
    private BigDecimal balance;
    private WalletStatus status;

    // Used later for wallet-level locking in concurrent transactions
    private transient ReentrantLock lock = new ReentrantLock();

    public Wallet() {
    }

    public Wallet(String walletId, String ownerName, BigDecimal balance, WalletStatus status) {
        this.walletId = walletId;
        this.ownerName = ownerName;
        this.balance = balance;
        this.status = status;
        this.lock = new ReentrantLock();
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public WalletStatus getStatus() {
        return status;
    }

    public void setStatus(WalletStatus status) {
        this.status = status;
    }

    public ReentrantLock getLock() {
        if (lock == null) {
            lock = new ReentrantLock();
        }
        return lock;
    }

    public void setLock(ReentrantLock lock) {
        this.lock = lock;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "walletId='" + walletId + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", balance=" + balance +
                ", status=" + status +
                '}';
    }
}