package sh.zoltus.onecore.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitScheduler;
import org.sqlite.SQLiteConfig;
import sh.zoltus.onecore.OneCore;
import sh.zoltus.onecore.OneEconomy;
import sh.zoltus.onecore.configuration.yamls.Config;
import sh.zoltus.onecore.player.command.User;
import sh.zoltus.onecore.player.command.commands.Spawn;
import sh.zoltus.onecore.player.command.commands.Warp;
import sh.zoltus.onecore.utils.PreLocation;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Database {

    @Getter
    @Accessors(fluent = true)
    private static Database database;

    private Connection connection;
    private final OneCore plugin;
    private final BukkitScheduler scheduler = Bukkit.getScheduler();
    private final int saveInterval = Config.DATA_SAVE_INTERVAL.getInt();
    private final String fileName = Config.DATABASE_NAME.getString();
    private final SQLiteConfig config = new SQLiteConfig();

    private Database(OneCore plugin) {
        Bukkit.getConsoleSender().sendMessage("Loading database...");
        this.plugin = plugin;
        this.connection = connection(); //test
        this.config.setJournalMode(SQLiteConfig.JournalMode.WAL);
        this.config.setTempStore(SQLiteConfig.TempStore.MEMORY);
        this.config.setSynchronous(SQLiteConfig.SynchronousMode.OFF);
        createTables();
        backupTimer(); //Starts backup timer
        // System.out.println("Loading server settings...");
        loadServerSettings();
        initAutoSaver();
    }

    public static Database init(OneCore plugin) {
        return database = database == null ? new Database(plugin) : database;
    }

    @SneakyThrows
    private Connection connection() {
        try {
            String url = "jdbc:sqlite:" + plugin.getDataFolder() + "/" + fileName + ".db";
            return connection = connection == null || connection.isClosed() ? DriverManager.getConnection(url, config.toProperties()) : connection;
        } catch (SQLException e) {
            throw new SQLException("§4Database connection failed!\n §c" + e.getMessage());
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
            System.out.println("§4Database table creation failed!\n §c" + e.getMessage());
        }
    }

    private void initAutoSaver() {
        scheduler.runTaskTimerAsynchronously(plugin, () -> {
            saveUsersAsync();
            saveServerAsync();
            saveEconomyAsync();
        }, (saveInterval * 20L) * 60, (saveInterval * 20L) * 60);
    }

    public void loadServerSettingsAsync() { //todo
        //CompletableFuture.completedFuture
        //CompletableFuture.runAsync(() -> 213)
        scheduler.runTaskAsynchronously(plugin, this::loadServerSettings);
    }

    private void loadServerSettings() {
        Gson gson = new Gson();
        Type type = new HashMapTypeToken().getType();

        try (Connection con = connection()
             ; PreparedStatement st = con.prepareStatement("SELECT * FROM Server")
             ; ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                String key = rs.getString("key");
                String value = rs.getString("value");
                switch (key) {
                    case "Spawn" -> Spawn.setSpawn(gson.fromJson(value, PreLocation.class));
                    case "Motd" -> {
                        //todo motd String motd = value;
                    }
                    case "Warps" -> Warp.setWarps(gson.fromJson(value, type));
                }
            }
        } catch (SQLException e) {
            System.out.println("§4Error loading server settings!\n §c" + e.getMessage());
            e.printStackTrace();
        }
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
            System.out.println("§4Error saving players!\n §c" + e.getMessage());
        }
    }

    public void saveServerAsync() {
        scheduler.runTaskAsynchronously(plugin, this::saveServer);
    }

    public void saveServer() {
        try (Connection con = connection();
             PreparedStatement pStm = con.prepareStatement("INSERT OR REPLACE INTO Server(key,value) VALUES(?,?)")) {
            Gson gson = new Gson();
            //Spawn
            pStm.setString(1, "Spawn");
            pStm.setString(2, gson.toJson(Spawn.getSpawn()));
            pStm.addBatch();
            //Motd
            pStm.setString(1, "Motd");
            pStm.setString(2, Bukkit.getMotd());
            pStm.addBatch();
            //Warps
            pStm.setString(1, "Warps");
            pStm.setString(2, gson.toJson(Warp.getWarps()));
            pStm.addBatch();
            pStm.executeBatch();
        } catch (SQLException e) {
            System.out.println("§4Error saving server settings!\n §c" + e.getMessage());
        }
    }

    //todo if economy disabled it would return
    public void saveEconomyAsync() {
        scheduler.runTaskAsynchronously(plugin, this::saveEconomy);
    }

    public void saveEconomy() {
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
            System.out.println("§4Error saving economy!\n §c" + e.getMessage());
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
                System.out.println("§4Error loading economy: \n §c" + e.getMessage());
            }
        });
    }

    /**
     * Use only on async.
     * <p>
     * Should only be executed async
     *
     * @param offP offlinePlayer
     * @return OneUser
     */
    public boolean loadPlayer(OfflinePlayer offP) {
        String uuid = offP.getUniqueId().toString();

        //if target is loaded it returns, Not needed just incase.
        if (User.ofNullable(offP) != null) {
            return true;
        }

        try (Connection con = connection()
             ; PreparedStatement pStm = con.prepareStatement("SELECT data FROM Players WHERE uuid = ?")) {
            pStm.setString(1, uuid);
            try (ResultSet rs = pStm.executeQuery()) {
                if (!rs.next()) { //Goes here if new user onasyncprelogin
                    return false;
                } else {
                    GsonBuilder builder = new GsonBuilder();
                    builder.registerTypeAdapter(User.class, new OneUserAdapter(offP));
                    Gson gson = builder.create();
                    Bukkit.getConsoleSender().sendMessage("§bFrom db");
                    gson.fromJson(rs.getString("data"), User.class);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("§4Error loading user: " + offP.getName() + "\n §c" + e.getMessage());
            return false;
        }
    }

    private static int UPTIMEHOURS = 0;

    //Backups based time from startup
    private void backupTimer() {
        List<Integer> hours = List.of(1, 5, 12, 24, 48); //todo hours to config and uptime interval possibly,to singleton
        scheduler.runTaskTimerAsynchronously(plugin, () -> {
            //backup on start
            if (UPTIMEHOURS == 0) {
                backup("start");
            } else {
                hours.stream() //backup every hour which is marked on list
                        .filter(integer -> UPTIMEHOURS % integer == 0)
                        .forEach(integer -> backup(integer + "h"));
            }
            //Yamls.COMMANDS.getYml().getIntegerList("")
            UPTIMEHOURS++;
        }, 0, 72000); //Every hour
    }

    private void backup(String time) {
        final Path source = Paths.get(plugin.getDataFolder() + "/" + fileName + ".db");
        final Path target = Paths.get(plugin.getDataFolder() + "/backups/" + fileName + "(" + time + ").db");
        final Path f = target.getParent();
        try {
            if (!Files.exists(f) || (Files.exists(f) && !Files.isDirectory(f))) {
                Files.createDirectory(f);
            }
            //todo test if backup is done
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.out.println("§4Error backing up database!\n §c" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveAll() {
        saveUsers();
        saveEconomy();
        saveServer();
    }
    private static class HashMapTypeToken extends TypeToken<HashMap<String, PreLocation>> {
    }
}
