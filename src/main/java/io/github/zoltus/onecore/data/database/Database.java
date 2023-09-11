package io.github.zoltus.onecore.data.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.github.zoltus.onecore.economy.OneEconomy;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.teleporting.PreLocation;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitScheduler;
import org.sqlite.SQLiteConfig;

import java.sql.*;
import java.util.HashMap;
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
        this.connection = connection(); //test
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
        }                // Balance
    }

    private void createTables() {
        // Creates tables
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

    /*
    private void addColumnIfNotExists(Statement stmt, String tableName, String columnName, String columnDefinition) throws SQLException {
        ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + tableName + ");");
        boolean columnExists = false;
        while (rs.next()) {
            String existingColumnName = rs.getString("name");
            if (existingColumnName.equals(columnName)) {
                columnExists = true;
                break;
            }
        }

        if (!columnExists) {
            stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDefinition + ";");
        }
    }*/

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
        //todo save balance from balances map if use oneeconomy
        final String sqlHomes = "INSERT INTO homes (uuid, name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        final String sqlBalances = "INSERT INTO balances (uuid, balance) VALUES (?, ?)";

        try (Connection con = connection()
             ; PreparedStatement pStmPlayers = con.prepareStatement(sqlPlayers)
             ; PreparedStatement pStmHomes = con.prepareStatement(sqlHomes)
             ; PreparedStatement pStmBalances = con.prepareStatement(sqlBalances)
        ) {
            //todo balances load separate

            // Save balances
            if (Config.ECONOMY_USE_ONEECONOMY.getBoolean()) {
                for (Map.Entry<UUID, Double> entry : OneEconomy.getBalances().entrySet()) {
                    Double bal = entry.getValue();
                    String uuid = entry.getKey().toString();
                    pStmBalances.setString(1, uuid);
                    pStmBalances.setDouble(2, bal);
                    pStmBalances.addBatch();
                }
            }
            //Save user homes and data
            for (Map.Entry<UUID, User> entry : User.getUsers().entrySet()) {
                User user = entry.getValue();
                UUID uuid = user.getUniqueId();
                // Player data
                pStmPlayers.setString(1, uuid.toString());
                pStmPlayers.setBoolean(2, user.isTpEnabled());
                pStmPlayers.setBoolean(3, user.isVanished());
                pStmPlayers.executeUpdate();
                // Homes
                for (Map.Entry<String, PreLocation> homeEntry : user.getHomes().entrySet()) {
                    PreLocation home = homeEntry.getValue();
                    pStmHomes.setString(1, uuid.toString());
                    pStmHomes.setString(2, homeEntry.getKey());
                    pStmHomes.setString(3, home.getWorldName());
                    pStmHomes.setDouble(4, home.getX());
                    pStmHomes.setDouble(5, home.getY());
                    pStmHomes.setDouble(6, home.getZ());
                    pStmHomes.setDouble(7, home.getYaw());
                    pStmHomes.setDouble(8, home.getPitch());
                    pStmHomes.addBatch();
                }
                //Add Batch
                pStmBalances.executeBatch();
                pStmPlayers.addBatch();
                pStmHomes.executeBatch();
            }
            pStmPlayers.executeBatch();
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
        try (Connection con = connection()
             ; PreparedStatement pStm = con.prepareStatement(sql)
             ; ResultSet rs = pStm.executeQuery()) {
            int index = 0;
            while (rs.next()) {
                String uuid = rs.getString("uuid");
                OfflinePlayer offP = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                if (User.of(offP) == null) { //If user havent been loaded yet by login it loads it
                    userFromResult(offP, rs); // without this loading could happen twice
                }
                index++;
            }
            plugin.getLogger().info("Finished caching " + index + " users, took: " + (System.currentTimeMillis() - l) + "ms");
        } catch (SQLException e) {
            throw new DatabaseException("§4Error caching users: \n §c" + e.getMessage());
        }
    }

    private void userFromResult(OfflinePlayer offP, ResultSet rs) throws SQLException {
        boolean isvanished = rs.getBoolean("isvanished");
        boolean tpenabled = rs.getBoolean("tpenabled");
        String homes = rs.getString("homes");
        double balance = rs.getDouble("balance");
        User newUser = new User(offP);
        newUser.setVanished(isvanished);
        newUser.setBalance(balance);
        newUser.setTpEnabled(tpenabled);
        Gson gson = new Gson();
        if (homes != null) {
            newUser.setHomes(gson.fromJson(homes, new TypeToken<HashMap<String, PreLocation>>() {
            }.getType()));
        }
    }

    public static class DatabaseException extends RuntimeException {
        public DatabaseException(String message) {
            super(message);
        }
    }
}
