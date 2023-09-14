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
            String players = """
                    CREATE TABLE IF NOT EXISTS players (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        uuid       CHAR(36) NOT NULL, UNIQUE,
                        tpenabled  BOOLEAN NOT NULL DEFAULT 0,
                        isvanished BOOLEAN NOT NULL DEFAULT 0
                    );
                    """;
            String balances = """
                    CREATE TABLE IF NOT EXISTS balances (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        uuid    CHAR(36) NOT NULL, UNIQUE,
                        balance DOUBLE NOT NULL,
                        FOREIGN KEY (uuid) REFERENCES players(uuid)
                            ON DELETE RESTRICT
                            ON UPDATE CASCADE
                    );
                    """;
            String homes = """
                    CREATE TABLE IF NOT EXISTS homes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        uuid  CHAR(36) NOT NULL, UNIQUE,
                        name  VARCHAR(16) NOT NULL,
                        world VARCHAR(255) NOT NULL,
                        x     DOUBLE NOT NULL,
                        y     DOUBLE NOT NULL,
                        z     DOUBLE NOT NULL,
                        yaw   FLOAT NOT NULL,
                        pitch FLOAT NOT NULL,
                        FOREIGN KEY (uuid) REFERENCES players(uuid)
                            ON DELETE RESTRICT
                            ON UPDATE CASCADE
                    );
                    """;
            stmt.execute(players);
            stmt.execute(balances);
            stmt.execute(homes);
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
        final String sqlPlayers = "INSERT OR REPLACE INTO players(uuid, tpenabled, isvanished) VALUES (?, ?, ?)";
        final String sqlHomes = "INSERT OR REPLACE INTO homes(uuid, name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        final String sqlBalances = "INSERT OR REPLACE INTO balances(uuid, balance) VALUES (?, ?)";
        try (Connection con = connection()
             ; PreparedStatement playerStmt = con.prepareStatement(sqlPlayers)
             ; PreparedStatement homeStmt = con.prepareStatement(sqlHomes)
             ; PreparedStatement balanceStmt = con.prepareStatement(sqlBalances)) {
            //Save user homeStmt and data
            for (Map.Entry<UUID, User> entry : User.getUsers().entrySet()) {
                User user = entry.getValue();
                String uuid = user.getUniqueId().toString();
                // Player data
                playerStmt.setString(1, uuid);
                playerStmt.setBoolean(2, user.isTpEnabled());
                playerStmt.setBoolean(3, user.isVanished());
                playerStmt.addBatch();
                // Saves economy if OneEconomy is enabled
                // Economy needs to be handled after player data is inserted
                if (Config.ECONOMY_USE_ONEECONOMY.getBoolean()) {
                    balanceStmt.setString(1, uuid);
                    balanceStmt.setDouble(2, user.getBalance());
                    balanceStmt.addBatch();
                }
                // Homes
                for (Map.Entry<String, PreLocation> homeEntry : user.getHomes().entrySet()) {
                    String name = homeEntry.getKey();
                    PreLocation home = homeEntry.getValue();
                    homeStmt.setString(1, user.getUniqueId().toString());
                    homeStmt.setString(2, name);
                    homeStmt.setString(3, home.getWorldName());
                    homeStmt.setDouble(4, home.getX());
                    homeStmt.setDouble(5, home.getY());
                    homeStmt.setDouble(6, home.getZ());
                    homeStmt.setFloat(7, home.getYaw());
                    homeStmt.setFloat(8, home.getPitch());
                    homeStmt.addBatch();
                }
            }
            playerStmt.executeBatch();
            balanceStmt.executeBatch();
            homeStmt.executeBatch();
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
        //Homes needs to be separate, otherwise it would return duplicate data
        String sqlHomes = "SELECT * from homes;";
        try (Connection con = connection()
             ; PreparedStatement playerStm = con.prepareStatement(sql)
             ; PreparedStatement homeStm = con.prepareStatement(sqlHomes)
             ; ResultSet rsPlayer = playerStm.executeQuery()
             ; ResultSet rshomes = homeStm.executeQuery()) {
            while (rsPlayer.next()) {
                String uuid = rsPlayer.getString("uuid");
                OfflinePlayer offP = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                User newUser = new User(offP);
                //String homes = rsPlayer.getString("homes");
                double balance = rsPlayer.getDouble("balance");
                boolean isvanished = rsPlayer.getBoolean("isvanished");
                boolean tpenabled = rsPlayer.getBoolean("tpenabled");
                newUser.setVanished(isvanished);
                newUser.setBalance(balance);
                newUser.setTpEnabled(tpenabled);
            }
            //Gets all homes and sets them for players
            while (rshomes.next()) {
                UUID uuid = UUID.fromString(rshomes.getString("uuid"));
                String homeName = rshomes.getString("name");
                String world = rshomes.getString("world");
                double x = rshomes.getDouble("x");
                double y = rshomes.getDouble("y");
                double z = rshomes.getDouble("z");
                float yaw = rshomes.getFloat("yaw");
                float pitch = rshomes.getFloat("pitch");
                PreLocation preLoc = new PreLocation(world, x, y, z, yaw, pitch);
                User user = User.of(uuid);
                user.setHome(homeName, preLoc);
            }
            plugin.getLogger().info("Finished caching " + User.getUsers().size()
                    + " users, took: " + (System.currentTimeMillis() - l) + "ms");
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
