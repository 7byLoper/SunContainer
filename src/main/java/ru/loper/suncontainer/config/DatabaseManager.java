package ru.loper.suncontainer.config;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.*;
import java.util.Objects;
import java.util.logging.Level;

public class DatabaseManager {
    private final Plugin plugin;
    private Connection connection;

    public DatabaseManager(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null");
    }

    public void connect() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            throw new IllegalStateException("Could not create plugin data folder");
        }

        File dbFile = new File(dataFolder, "database.db");
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            createTable();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }
    }

    public void disconnect() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to close database connection", e);
            }
        }
    }

    private void createTable() throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS player_containers (player VARCHAR(16) PRIMARY KEY, value INTEGER);")) {
            stmt.execute();
        }
    }

    private void checkConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
    }

    public void setValue(@NotNull String player, int value) {
        try {
            checkConnection();
            try (PreparedStatement stmt = connection.prepareStatement("INSERT OR REPLACE INTO player_containers (player, value) VALUES (?, ?);")) {
                stmt.setString(1, player.toLowerCase());
                stmt.setInt(2, value);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to set value for player: " + player, e);
        }
    }

    public int getValue(@NotNull String player) {
        try {
            checkConnection();
            try (PreparedStatement stmt = connection.prepareStatement("SELECT value FROM player_containers WHERE player = ?;")) {
                stmt.setString(1, player.toLowerCase());
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() ? rs.getInt("value") : 0;
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get value for player: " + player, e);
            return 0;
        }
    }
}