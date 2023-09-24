package io.github.zoltus.onecore.data.database;

import com.zaxxer.hikari.HikariDataSource;
import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.teleporting.PreLocation;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Map;
import java.util.UUID;

public abstract class Database {

    private final OneCore plugin;
    protected final String databaseURL;
    private final String createTables;
    private final String insertHomes;
    private final String insertPlayers;
    private final String insertBalances;
    private final String selectHomes;
    private final String selectPlayersAndBalances;

    private final HikariDataSource hikari;

    private Connection connection;
    private final BukkitScheduler scheduler = Bukkit.getScheduler();
    private final int saveInterval = Config.DB_SAVE_INTERVAL.getInt();
    private final int port = Config.DB_PORT.getInt();
    private final String userName = Config.DB_USERNAME.getString();
    private final String password = Config.DB_PASSWORD.getString();
    private final String database = Config.DB_DATABASE.getString();
    private final String address = Config.DB_ADDRESS.getString();

    protected Database(OneCore plugin,
                       String databaseURL,
                       String createTables,
                       String insertHomes,
                       String insertPlayers,
                       String insertBalances,
                       String selectHomes,
                       String selectPlayersAndBalances) {
        this.plugin = plugin;
        this.databaseURL = databaseURL;
        this.createTables = createTables;
        this.insertHomes = insertHomes;
        this.insertPlayers = insertPlayers;
        this.insertBalances = insertBalances;
        this.selectHomes = selectHomes;
        this.selectPlayersAndBalances = selectPlayersAndBalances;
        this.hikari = initHikari();

        createTables();
        initAutoSaver();
    }

    public abstract HikariDataSource initHikari();

    public void closeConnectionPool() {
        if (hikari != null && !hikari.isClosed()) {
            hikari.close();
        }
    }

    private void initAutoSaver() {
        scheduler.runTaskTimerAsynchronously(plugin, this::saveUsersAsync,
                (saveInterval * 20L) * 60, (saveInterval * 20L) * 60);
    }

    public void saveUsersAsync() {
        scheduler.runTaskAsynchronously(plugin, this::saveData);
    }

    /*
     * mysql needs auto_increment
     * sqlite needs autoincrement
     */
    private void createTables() {
        try (Connection con = hikari.getConnection(); Statement stmt = con.createStatement()) {
            String[] queries = getQuery(createTables);
            for (String query : queries) {
                stmt.addBatch(query);
            }
            stmt.executeBatch();
        } catch (SQLException ex) {
            throw new Database.DatabaseException("§4Database table creation failed!\n §c" + ex.getMessage());
        }
    }

    /**
     * Saves users which has been edited to database there has been changes on their data
     */
    public void saveData() {
        final String sqlPlayers = getQuery(insertPlayers)[0];
        final String sqlHomes = getQuery(insertHomes)[0];
        final String sqlBalances = getQuery(insertBalances)[0];
        try (Connection con = hikari.getConnection()
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
            throw new Database.DatabaseException("§4Error saving players!\n §c" + e.getMessage());
        }
    }


    public void cacheData() {
        long l = System.currentTimeMillis();
        plugin.getLogger().info("Caching users");
        String sql = getQuery(selectPlayersAndBalances)[0];
        //Homes needs to be separate, otherwise it would return duplicate data.
        String sqlHomes = getQuery(selectHomes)[0];
        try (Connection con = hikari.getConnection()
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
            throw new Database.DatabaseException("§4Error caching users: \n §c" + e.getMessage());
        }
    }

    private String[] getQuery(String file) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(file)) {
            if (in == null) {
                throw new IOException("InputStream is null. File might not be found.");
            } else {
                return new String(in.readAllBytes()).split(";");
            }
        } catch (IOException ex) {
            throw new Database.DatabaseException("§4Database failed to get query!\n §c" + ex.getMessage());
        }
    }

    //Todo runtime? switch to normal?
    public static class DatabaseException extends RuntimeException {
        public DatabaseException(String message) {
            super(message);
        }
    }
}
