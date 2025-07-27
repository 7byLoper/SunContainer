package ru.loper.suncontainer.api.database;

import lombok.RequiredArgsConstructor;
import ru.loper.suncontainer.api.storage.ContainersStorage;
import ru.loper.suncore.api.database.DataBaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
public class ContainersDatabase implements ContainersStorage {
    private final DataBaseManager dbManager;

    @Override
    public void createTable() {
        String sql = dbManager.getSqlByDataType(
                "CREATE TABLE IF NOT EXISTS player_containers (" +
                "player_uuid VARCHAR(36) PRIMARY KEY, " +
                "containers INT NOT NULL DEFAULT 0)",
                "CREATE TABLE IF NOT EXISTS player_containers (" +
                "player_uuid TEXT PRIMARY KEY, " +
                "containers INTEGER NOT NULL DEFAULT 0)"
        );

        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create containers table", e);
        }
    }

    @Override
    public int getContainers(String player) {
        String sql = "SELECT containers FROM player_containers WHERE player_uuid = ?";

        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("containers");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get containers for player " + player, e);
        }
        return 0;
    }

    @Override
    public void setContainers(String player, int containers) {
        String sql = dbManager.getSqlByDataType(
                "INSERT INTO player_containers (player_uuid, containers) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE containers = ?",
                "INSERT OR REPLACE INTO player_containers (player_uuid, containers) VALUES (?, ?)"
        );

        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player);
            statement.setInt(2, containers);

            if (dbManager.getDataType().equals("mysql")) {
                statement.setInt(3, containers);
            }

            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to set containers for player " + player, e);
        }
    }

    @Override
    public void takeContainers(String player, int containers) {
        String sql = dbManager.getSqlByDataType(
                "UPDATE player_containers SET containers = GREATEST(0, containers - ?) WHERE player_uuid = ?",
                "UPDATE player_containers SET containers = MAX(0, containers - ?) WHERE player_uuid = ?"
        );

        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, containers);
            statement.setString(2, player);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                setContainers(player, 0);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove containers for player " + player, e);
        }
    }

    @Override
    public void addContainers(String player, int containers) {
        String sql = dbManager.getSqlByDataType(
                "INSERT INTO player_containers (player_uuid, containers) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE containers = containers + ?",
                "INSERT INTO player_containers (player_uuid, containers) VALUES (?, ?) " +
                "ON CONFLICT(player_uuid) DO UPDATE SET containers = containers + ?"
        );

        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player);
            statement.setInt(2, containers);
            statement.setInt(3, containers);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add containers for player " + player, e);
        }
    }
}