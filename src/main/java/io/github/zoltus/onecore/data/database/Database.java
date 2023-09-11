package io.github.zoltus.onecore.data.database;

import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.teleporting.PreLocation;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitScheduler;
import org.sqlite.SQLiteConfig;

import java.sql.*;
import java.util.Map;
import java.util.UUID;

public class Database {
    private Connection connection;
    private final OneCore plugin;
    private final BukkitScheduler scheduler = Bukkit.getScheduler();
    private final int saveInterval = Config.DATA_SAVE_INTERVAL.getInt();
    private final SQLiteConfig config = new SQLiteConfig();

    private Database(OneCore plugin) {
        plugin.getLogger().info("Loading database...");
        this.plugin = plugin;
        this.connection = connection();
        //todo if these are normal settings anyways
        // "PRAGMA mmap_size = 30000000000;
        this.config.setJournalMode(SQLiteConfig.JournalMode.WAL);
        this.config.setTempStore(SQLiteConfig.TempStore.MEMORY);
        this.config.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
        this.config.setPragma(SQLiteConfig.Pragma.FOREIGN_KEYS, "1");
        createTables();
        initAutoSaver();
    }

    public static Database init(OneCore plugin) {
        Database database = plugin.getDatabase();
        return database == null ? new Database(plugin) : database;
    }

    private Connection connection() {
        try {
            String url = "jdbc:sqlite:" + plugin.getDataFolder() + "/database.db";
            return connection = connection == null
                    || connection.isClosed() ? DriverManager.getConnection(url, config.toProperties()) : connection;
        } catch (Exception e) {
            throw new DatabaseException("§4Database connection failed!\n §c" + e.getMessage());
        }
    }

    private void createTables() {
        try (Connection con = connection(); Statement stmt = con.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS players(
                    uuid       VARCHAR(36) PRIMARY KEY,
                    tpenabled  BOOLEAN NOT NULL DEFAULT 0,
                    isvanished BOOLEAN NOT NULL DEFAULT 0
                    );
                    CREATE TABLE IF NOT EXISTS balances(
                        uuid    VARCHAR(36) PRIMARY KEY,
                        balance DOUBLE NOT NULL,
                        FOREIGN KEY balances(uuid) REFERENCES players(uuid)
                            ON DELETE RESTRICT
                            ON UPDATE CASCADE
                    );         
                    CREATE TABLE IF NOT EXISTS homes(
                        uuid  VARCHAR(36),
                        NAME  VARCHAR(16),
                        world CHAR   NOT NULL,
                        x     DOUBLE NOT NULL,
                        y     DOUBLE NOT NULL,
                        z     DOUBLE NOT NULL,
                        yaw   DOUBLE NOT NULL,
                        pitch DOUBLE NOT NULL,
                        PRIMARY KEY (uuid, NAME),
                        FOREIGN KEY homes(uuid) REFERENCES players(uuid)
                            ON DELETE RESTRICT
                            ON UPDATE CASCADE
                    );
                    """);
        } catch (SQLException e) {
            throw new DatabaseException("§4Database table creation failed!\n §c" + e.getMessage());
        }
    }

    private void initAutoSaver() {
        scheduler.runTaskTimerAsynchronously(plugin, this::saveUsersAsync,
                (saveInterval * 20L) * 60, (saveInterval * 20L) * 60);
    }

    public void saveUsersAsync() {
        scheduler.runTaskAsynchronously(plugin, this::saveData);
    }

    /**
     * Saves users which has been edited to database there has been changes on their data
     */
    public void saveData() {
        final String sqlPlayers = "INSERT INTO players (uuid, tpenabled, isvanished) VALUES (?, ?, ?)";
        final String sqlHomes = "INSERT INTO homes (uuid, name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        final String sqlBalances = "INSERT INTO balances (uuid, balance) VALUES (?, ?)";
        try (Connection con = connection()
             ; PreparedStatement playerStmt = con.prepareStatement(sqlPlayers)
             ; PreparedStatement homeStmt = con.prepareStatement(sqlHomes)
             ; PreparedStatement balanceStmt = con.prepareStatement(sqlBalances)) {
            //Save user homeStmt and data
            for (Map.Entry<UUID, User> entry : User.getUsers().entrySet()) {
                User user = entry.getValue();
                String uuid = user.getUniqueId().toString();
                // Saves economy if OneEconomy is enabled
                if (Config.ECONOMY_USE_ONEECONOMY.getBoolean()) {
                    balanceStmt.setString(1, uuid);
                    balanceStmt.setDouble(2, user.getBalance());
                    balanceStmt.addBatch();
                }
                // Player data
                playerStmt.setString(1, uuid);
                playerStmt.setBoolean(2, user.isTpEnabled());
                playerStmt.setBoolean(3, user.isVanished());
                playerStmt.addBatch();
                // Homes
                for (Map.Entry<String, PreLocation> homeEntry : user.getHomes().entrySet()) {
                    PreLocation home = homeEntry.getValue();
                    homeStmt.setString(1, user.getUniqueId().toString());
                    homeStmt.setString(2, homeEntry.getKey());
                    homeStmt.setString(3, home.getWorldName());
                    homeStmt.setDouble(4, home.getX());
                    homeStmt.setDouble(5, home.getY());
                    homeStmt.setDouble(6, home.getZ());
                    homeStmt.setDouble(7, home.getYaw());
                    homeStmt.setDouble(8, home.getPitch());
                    homeStmt.addBatch();
                }
                playerStmt.addBatch();
            }
            homeStmt.executeBatch();
            balanceStmt.executeBatch();
            playerStmt.executeBatch();
        } catch (SQLException e) {
            throw new DatabaseException("§4Error saving players!\n §c" + e.getMessage());
        }
    }


    public void cacheData() {
        long l = System.currentTimeMillis();
        plugin.getLogger().info("Caching users");
        String sql = """
                SELECT players.uuid, tpenabled, isvanished, balances.balance
                    FROM players LEFT JOIN balances ON players.uuid = balances.uuid;""";

        String sqlHomes = "SELECT * from homes;";

        try (Connection con = connection()
             ; PreparedStatement pStm = con.prepareStatement(sql)
             ; PreparedStatement pStm = con.prepareStatement(sqlHomes)
             //todo homes
             ; ResultSet rs = pStm.executeQuery()) {
            int index = 0;
            while (rs.next()) {
                String uuid = rs.getString("uuid");
                OfflinePlayer offP = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                User newUser = new User(offP);

                //String homes = rs.getString("homes");
                double balance = rs.getDouble("balance");
                boolean isvanished = rs.getBoolean("isvanished");
                boolean tpenabled = rs.getBoolean("tpenabled");
                newUser.setVanished(isvanished);
                newUser.setBalance(balance);
                newUser.setTpEnabled(tpenabled);
                index++;
            }
            plugin.getLogger().info("Finished caching " + index + " users, took: " + (System.currentTimeMillis() - l) + "ms");
        } catch (SQLException e) {
            throw new DatabaseException("§4Error caching users: \n §c" + e.getMessage());
        }
    }


    public static class DatabaseException extends RuntimeException {
        public DatabaseException(String message) {
            super(message);
        }
    }
}
