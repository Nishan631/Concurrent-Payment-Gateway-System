package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import enums.WalletStatus;
import model.Wallet;

public class WalletDAO {
	public boolean createWallet(Wallet wallet) {
        String sql = "INSERT INTO wallets (wallet_id, owner_name, balance, status) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, wallet.getWalletId());
            ps.setString(2, wallet.getOwnerName());
            ps.setBigDecimal(3, wallet.getBalance());
            ps.setString(4, wallet.getStatus().name());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Wallet getWalletById(String walletId) {
        String sql = "SELECT wallet_id, owner_name, balance, status FROM wallets WHERE wallet_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, walletId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Wallet wallet = new Wallet();
                    wallet.setWalletId(rs.getString("wallet_id"));
                    wallet.setOwnerName(rs.getString("owner_name"));
                    wallet.setBalance(rs.getBigDecimal("balance"));
                    wallet.setStatus(WalletStatus.valueOf(rs.getString("status")));
                    return wallet;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Wallet> getAllWallets() {
        String sql = "SELECT wallet_id, owner_name, balance, status FROM wallets";
        List<Wallet> wallets = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Wallet wallet = new Wallet();
                wallet.setWalletId(rs.getString("wallet_id"));
                wallet.setOwnerName(rs.getString("owner_name"));
                wallet.setBalance(rs.getBigDecimal("balance"));
                wallet.setStatus(WalletStatus.valueOf(rs.getString("status")));
                wallets.add(wallet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return wallets;
    }

    public boolean updateWalletBalance(String walletId, BigDecimal newBalance) {
        String sql = "UPDATE wallets SET balance = ? WHERE wallet_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBigDecimal(1, newBalance);
            ps.setString(2, walletId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateWalletStatus(String walletId, WalletStatus status) {
        String sql = "UPDATE wallets SET status = ? WHERE wallet_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setString(2, walletId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean walletExists(String walletId) {
        String sql = "SELECT 1 FROM wallets WHERE wallet_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, walletId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteWallet(String walletId) {
        String sql = "DELETE FROM wallets WHERE wallet_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, walletId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
