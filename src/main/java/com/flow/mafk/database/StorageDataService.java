package com.flow.mafk.database;

import com.flow.mafk.Mafk;
import com.flow.mafk.database.mysql.DBConnection;
import com.flow.mafk.util.object.User;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class StorageDataService {

    public static void initializeTables() {
        try (Connection connection = DBConnection.getConnection()) {
            try (Statement stmt = connection.createStatement()) {

                stmt.execute("CREATE TABLE IF NOT EXISTS afk_data (" +
                        "user_id VARCHAR(36) PRIMARY KEY, " +
                        "point DOUBLE)");


            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static CompletableFuture<User> loadUserData(UUID userId) {
        CompletableFuture<User> future = new CompletableFuture<>();

        Mafk.runAsync(() -> {
            try (Connection connection = DBConnection.getConnection()) {
                PreparedStatement stmt = connection.prepareStatement("SELECT point FROM afk_data WHERE user_id = ?");
                stmt.setString(1, userId.toString());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    Double point = rs.getDouble("point");
                    future.complete(new User(userId, false, point));
                } else {
                    future.complete(new User(userId, false, 0.0));
                }
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public static Optional<User> loadUserDataSync(UUID userId) {
        try (Connection connection = DBConnection.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT point FROM afk_data WHERE user_id = ?");
            stmt.setString(1, userId.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Double point = rs.getDouble("point");
                return Optional.of(new User(userId, false, point));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static CompletableFuture<Boolean> saveUserData(User user) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        Mafk.runAsync(() -> {
            String insertQuery = "INSERT INTO afk_data (user_id, point) VALUES (?, ?) ON DUPLICATE KEY UPDATE point = ?";

            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

                insertStmt.setString(1, user.getUuid().toString());
                insertStmt.setDouble(2, user.getAfkPoint());
                insertStmt.setDouble(3, user.getAfkPoint());

                insertStmt.executeUpdate();
                future.complete(true);

            } catch (SQLException e) {
                e.printStackTrace();
                future.complete(false);
            }
        });

        return future;
    }

    public static boolean saveUserDataSync(User user) {
        String insertQuery = "INSERT INTO afk_data (user_id, point) VALUES (?, ?) ON DUPLICATE KEY UPDATE point = ?";


        try (Connection connection = DBConnection.getConnection();
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

            insertStmt.setString(1, user.getUuid().toString());
            insertStmt.setDouble(2, user.getAfkPoint());
            insertStmt.setDouble(3, user.getAfkPoint());

            // 쿼리 실행
            insertStmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
