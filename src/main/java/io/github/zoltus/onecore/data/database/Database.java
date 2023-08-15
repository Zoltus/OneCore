package io.github.zoltus.onecore.data.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
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
        }
    }

    private void createTables() {
        // Creates tables
        try (Connection con = connection(); Statement stmt = con.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS player(
                    uuid VARCHAR(36) NOT NULL UNIQUE,
                    tpenabled BOOLEAN NOT NULL DEFAULT 1,
                    balance DOUBLE NOT NULL DEFAULT 0,
                    homes TEXT,
                    isvanished BOOLEAN NOT NULL DEFAULT 0,
                    PRIMARY KEY (uuid)
                );
                """);
            // Add the new column if it doesn't exist
            addColumnIfNotExists(stmt, "player", "isvanished", "BOOLEAN NOT NULL DEFAULT 0");
        } catch (SQLException e) {
            throw new DatabaseException("§4Database table creation failed!\n §c" + e.getMessage());
        }
    }

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
    }

    private void initAutoSaver() {
        scheduler.runTaskTimerAsynchronously(plugin, this::saveUsersAsync,
                (saveInterval * 20L) * 60, (saveInterval * 20L) * 60);
    }

    public void saveUsersAsync() {
        scheduler.runTaskAsynchronously(plugin, this::saveUsers);
    }

    /**
     * Saves users which has been edited to database there has been changes on their data
     */
    public void saveUsers() {
        final String sql = "INSERT OR REPLACE INTO player(uuid, tpenabled, homes, balance, isvanished) VALUES(?,?,?,?,?)";
        try (Connection con = connection()
             ; PreparedStatement pStm = con.prepareStatement(sql)) {
            for (Map.Entry<UUID, User> entry : User.getUsers().entrySet()) {
                User user = entry.getValue();
                userToDatabase(user, pStm);
                pStm.addBatch();
            }
            pStm.executeBatch();
        } catch (SQLException e) {
            throw new DatabaseException("§4Error saving players!\n §c" + e.getMessage());
        }
    }

    private void userToDatabase(User user, PreparedStatement pStm) throws SQLException {
        UUID uuid = user.getUniqueId();
        pStm.setString(1, uuid.toString());
        pStm.setBoolean(2, user.isTpEnabled());
        String homes = new Gson().toJson(user.getHomes(), HashMap.class);
        pStm.setString(3, homes);
        pStm.setDouble(4, user.getBalance());
        pStm.setBoolean(5, user.isVanished());
    }

    public void cacheUsers() {
        long l = System.currentTimeMillis();
        plugin.getLogger().info("Caching users");
        try (Connection con = connection()
             ; PreparedStatement pStm = con.prepareStatement("SELECT * FROM player")
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
