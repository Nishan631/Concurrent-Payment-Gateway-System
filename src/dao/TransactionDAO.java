package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import enums.TransactionStatus;
import model.Transaction;

public class TransactionDAO {
	public boolean insertTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (transaction_id, source_wallet_id, destination_wallet_id, amount, status, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, transaction.getTransactionId());
            ps.setString(2, transaction.getSourceWalletId());
            ps.setString(3, transaction.getDestinationWalletId());
            ps.setBigDecimal(4, transaction.getAmount());
            ps.setString(5, transaction.getStatus().name());
            ps.setTimestamp(6, java.sql.Timestamp.valueOf(transaction.getCreatedAt()));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateTransactionStatus(String transactionId, TransactionStatus status) {
        String sql = "UPDATE transactions SET status = ? WHERE transaction_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setString(2, transactionId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Transaction getTransactionById(String transactionId) {
        String sql = "SELECT transaction_id, source_wallet_id, destination_wallet_id, amount, status, created_at " +
                     "FROM transactions WHERE transaction_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, transactionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setTransactionId(rs.getString("transaction_id"));
                    transaction.setSourceWalletId(rs.getString("source_wallet_id"));
                    transaction.setDestinationWalletId(rs.getString("destination_wallet_id"));
                    transaction.setAmount(rs.getBigDecimal("amount"));
                    transaction.setStatus(TransactionStatus.valueOf(rs.getString("status")));

                    java.sql.Timestamp timestamp = rs.getTimestamp("created_at");
                    if (timestamp != null) {
                        transaction.setCreatedAt(timestamp.toLocalDateTime());
                    }

                    return transaction;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Transaction> getAllTransactions() {
        String sql = "SELECT transaction_id, source_wallet_id, destination_wallet_id, amount, status, created_at " +
                     "FROM transactions ORDER BY created_at DESC";

        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(rs.getString("transaction_id"));
                transaction.setSourceWalletId(rs.getString("source_wallet_id"));
                transaction.setDestinationWalletId(rs.getString("destination_wallet_id"));
                transaction.setAmount(rs.getBigDecimal("amount"));
                transaction.setStatus(TransactionStatus.valueOf(rs.getString("status")));

                java.sql.Timestamp timestamp = rs.getTimestamp("created_at");
                if (timestamp != null) {
                    transaction.setCreatedAt(timestamp.toLocalDateTime());
                }

                transactions.add(transaction);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public List<Transaction> getTransactionsByWalletId(String walletId) {
        String sql = "SELECT transaction_id, source_wallet_id, destination_wallet_id, amount, status, created_at " +
                     "FROM transactions " +
                     "WHERE source_wallet_id = ? OR destination_wallet_id = ? " +
                     "ORDER BY created_at DESC";

        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, walletId);
            ps.setString(2, walletId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setTransactionId(rs.getString("transaction_id"));
                    transaction.setSourceWalletId(rs.getString("source_wallet_id"));
                    transaction.setDestinationWalletId(rs.getString("destination_wallet_id"));
                    transaction.setAmount(rs.getBigDecimal("amount"));
                    transaction.setStatus(TransactionStatus.valueOf(rs.getString("status")));

                    java.sql.Timestamp timestamp = rs.getTimestamp("created_at");
                    if (timestamp != null) {
                        transaction.setCreatedAt(timestamp.toLocalDateTime());
                    }

                    transactions.add(transaction);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }
    
    public boolean isRollbackAlreadyDone(String originalTransactionId) {
        return false;
    }

    public void markTransactionAsRollbackReference(String newTransactionId, String originalTransactionId) {
        // Optional: implement later when rollback DB schema is finalized
    }
}