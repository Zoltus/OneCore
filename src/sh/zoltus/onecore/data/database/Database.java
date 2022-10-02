package sh.zoltus.onecore.data.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitScheduler;
import org.sqlite.SQLiteConfig;
import sh.zoltus.onecore.OneCore;
import sh.zoltus.onecore.data.configuration.yamls.Config;
import sh.zoltus.onecore.economy.OneEconomy;
import sh.zoltus.onecore.player.User;

import java.sql.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Database {
    private Connection connection;
    private final OneCore plugin;
    private final BukkitScheduler scheduler = Bukkit.getScheduler();
    private final int saveInterval = Config.DATA_SAVE_INTERVAL.getInt();
    private final String fileName = Config.DATABASE_NAME.getString();
    private final SQLiteConfig config = new SQLiteConfig();

    //todo async
    private Database(OneCore plugin) {
        Bukkit.getConsoleSender().sendMessage("Loading database...");
        this.plugin = plugin;
        this.connection = connection(); //test
        this.config.setJournalMode(SQLiteConfig.JournalMode.WAL);
        this.config.setTempStore(SQLiteConfig.TempStore.MEMORY);
        this.config.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
        createTables();
        // backupTimer(); //Starts backup timer
        // Bukkit.getConsoleSender().sendMessage("Loading server settings...");
        initAutoSaver();
    }

    public static Database init(OneCore plugin) {
        Database database = plugin.getDatabase();
        return database == null ? new Database(plugin) : database;
    }

    private Connection connection() {
        try {
            String url = "jdbc:sqlite:" + plugin.getDataFolder() + "/" + fileName + ".db";
            return connection = connection == null || connection.isClosed() ? DriverManager.getConnection(url, config.toProperties()) : connection;
        } catch (Exception e) {
            throw new RuntimeException("§4Database connection failed!\n §c" + e.getMessage());
        }
    }

    //Creates tables if doesnt exist async?
    private void createTables() {
        //@Language("SQLite")
        final String[] tables = { //todo join table uuid,name ect balance
                // "PRAGMA mmap_size = 30000000000;",
                //"PRAGMA foreign_keys = ON;",
                "CREATE TABLE IF NOT EXISTS Balances(" +
                        "uuid TEXT NOT NULL UNIQUE, " +
                        "name TEXT, " +
                        "balance REAL NOT NULL DEFAULT 0, " +
                        "PRIMARY KEY (uuid) " +
                        "); ",

                "CREATE TABLE IF NOT EXISTS Players(" +
                        "uuid TEXT NOT NULL UNIQUE, " +
                        "name TEXT, " +
                        "data TEXT, " +
                        "PRIMARY KEY (uuid) " +
                        "); ",

                "CREATE TABLE IF NOT EXISTS Server(" +
                        "key TEXT UNIQUE PRIMARY KEY, " +
                        "value TEXT);"
        };
        //Creates tables
        try (Connection con = connection()
             ; Statement stmt = con.createStatement()) {
            for (final String table : tables) {
                stmt.addBatch(table);
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("§4Database table creation failed!\n §c" + e.getMessage());
        }
    }

    private void initAutoSaver() {
        scheduler.runTaskTimerAsynchronously(plugin, () -> {
            saveUsersAsync();
            saveEconomyAsync();
        }, (saveInterval * 20L) * 60, (saveInterval * 20L) * 60);
    }

    public void saveUsersAsync() {
        scheduler.runTaskAsynchronously(plugin, this::saveUsers);
    }

    /**
     * Saves users which has been edited to database there has been changes on their data
     */
    public void saveUsers() {
        final String sql = "INSERT OR REPLACE INTO Players(uuid, name, data) VALUES(?,?,?)";
        try (Connection con = connection()
             ; PreparedStatement pStm = con.prepareStatement(sql)) {
            for (Map.Entry<UUID, User> entry : User.getUsers().entrySet()) {
                UUID uuid = entry.getKey();
                String uuidString = uuid.toString();
                User user = entry.getValue();
                GsonBuilder builder = new GsonBuilder().registerTypeAdapter(User.class, new OneUserAdapter(user.getOffP()));
                Gson gson = builder.create();
                pStm.setString(1, uuidString);
                pStm.setString(2, user.getName());
                pStm.setString(3, gson.toJson(user));
                pStm.addBatch();
                if (!user.isOnline() && !Config.KEEP_USERS_IN_CACHE.getBoolean()) {
                    User.getUsers().remove(uuid);
                }
            }
            pStm.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("§4Error saving players!\n §c" + e.getMessage());
        }
    }

    //todo if economy disabled it would return
    public void saveEconomyAsync() {
        scheduler.runTaskAsynchronously(plugin, this::saveEconomy);
    }

    public void saveEconomy() {
        if (plugin.getVault() != null) {
            try (Connection con = connection();
                 PreparedStatement pStm = con.prepareStatement("INSERT OR REPLACE INTO Balances(uuid,name,balance) VALUES(?,?,?)")) {
                for (Map.Entry<UUID, Double> entry : OneEconomy.getBalances().entrySet()) {
                    UUID uuid = entry.getKey();
                    pStm.setString(1, uuid.toString());
                    pStm.setString(2, Bukkit.getOfflinePlayer(uuid).getName());
                    pStm.setDouble(3, entry.getValue());
                    pStm.addBatch();
                }
                pStm.executeBatch();
            } catch (SQLException e) {
                throw new RuntimeException("§4Error saving economy!\n §c" + e.getMessage());
            }
        }
    }

    public void loadEconomyAsync() {
        scheduler.runTaskAsynchronously(plugin, () -> {
            try (Connection con = connection()
                 ; Statement st = con.createStatement()) {
                try (ResultSet rs = st.executeQuery("SELECT uuid, balance FROM Balances")) {
                    Bukkit.getConsoleSender().sendMessage("§bLoading economy"); //tdo economy loading from1 place
                    while (rs.next()) {
                        UUID uuid = UUID.fromString(rs.getString("uuid"));
                        double balance = rs.getDouble("balance");
                        OneEconomy.getBalances().put(uuid, balance);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("§4Error loading economy: \n §c" + e.getMessage());
            }
        });
    }

    /**
     *
     * @param offP offlinePlayer
     * @return OneUser
     */
    public CompletableFuture<User> loadUserAsync(OfflinePlayer offP) {
        return CompletableFuture.supplyAsync(() -> loadUser(offP));
    }

    public User loadUser(OfflinePlayer offP) {
        String uuid = offP.getUniqueId().toString();
        //todo check how parrots pl has done it
        //if target is loaded it returns, Not needed just incase.//todo check this
        //todo check playedbefore?
        User user = User.getUsers().get(offP.getUniqueId());
        if (user != null) {
            return user;
        } else {
            try (Connection con = connection()
                 ; PreparedStatement pStm = con.prepareStatement("SELECT data FROM Players WHERE uuid = ?")) {
                pStm.setString(1, uuid);
                try (ResultSet rs = pStm.executeQuery()) {
                    if (!rs.next()) { //Goes here if new user onasyncprelogin
                        return null;
                    } else {
                        GsonBuilder builder = new GsonBuilder();
                        builder.registerTypeAdapter(User.class, new OneUserAdapter(offP));
                        Gson gson = builder.create();
                        Bukkit.getConsoleSender().sendMessage("§bFrom db");
                        return gson.fromJson(rs.getString("data"), User.class);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("§4Error loading user: " + offP.getName() + "\n §c" + e.getMessage());
            }
        }
    }

    public void saveAll() {
        saveUsers();
        saveEconomy();
    }
}
