package sh.zoltus.onecore.player;

import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import sh.zoltus.onecore.OneCore;
import sh.zoltus.onecore.economy.OneEconomy;
import sh.zoltus.onecore.player.teleporting.LocationUtils;
import sh.zoltus.onecore.player.teleporting.PreLocation;
import sh.zoltus.onecore.player.teleporting.Request;
import sh.zoltus.onecore.player.teleporting.Teleport;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.HOME_AMOUNT_PERMISSION;
import static sh.zoltus.onecore.data.configuration.yamls.Config.START_MONEY;


@Data
@DatabaseTable(tableName = "users")
public class User {
    //todo remove transient
    @Getter
    private static final Map<UUID, User> users = new HashMap<>();
    private static OneCore plugin = OneCore.getPlugin();
    private static Economy economy = plugin.getVault();
    // private static Economy economy = economy;
    private transient final OfflinePlayer offP;
    private transient final List<Location> lastLocations = new ArrayList<>();
    private transient final List<Request> requests = new ArrayList<>();//todo tomap?

    //@DatabaseField(id = true)
    private final UUID uniqueId;
    //@DatabaseField(canBeNull = false, defaultValue = "true")
    private boolean tpEnabled = true;
    //@DatabaseField(dataType = DataType.SERIALIZABLE)
    private Map<String, PreLocation> homes = new HashMap<>();

    public User(OfflinePlayer offP) {
        this.offP = offP;
        this.uniqueId = offP.getUniqueId();
        //Default settings:
        users.put(uniqueId, this);
        //Bukkit.broadcastMessage("§cNew");
        //Sets balance to 0 if it doesnt exist, for toplist
        if (plugin.getVault() != null) {
            if (!OneEconomy.getBalances().containsKey(uniqueId)) {
                OneEconomy.getBalances().put(uniqueId, START_MONEY.getDouble());
            }
        }
    }

    /*
     OneUser -> visited -> map -> db -> new
     OneUser -> never -> null
     OneUser -> never -> new
     */

    /**
     * Only for OnlinePlayers
     *
     * @param p player
     * @return OneUser
     */
    public static User get(Player p) {
        return users.get(p.getUniqueId());
    }

    public static User get(OfflinePlayer offP) {
        return users.get(offP.getUniqueId());
    }

    public static CompletableFuture<User> loadAsync(OfflinePlayer offP) {
        return plugin.getDatabase().loadUserAsync(offP);
    }

    public void sendMessage(String message) {
        if (isOnline() && !message.isEmpty()) {
            this.getPlayer().sendMessage(message);
        }
    }

    public boolean isOnline() {
        return offP.isOnline();
    }

    public Player getPlayer() {
        return offP.getPlayer();
    }

    public String getName() {
        return this.offP.getName();
    }

    public void teleportTimer(Location loc) {
        Player p = getPlayer();
        if (p.hasPermission("bypass")) {
            LocationUtils.teleportSafeAsync(p, loc);
        } else {
            Teleport.start(this, null, loc);
        }
    }

    /*
     * Random
     */

    /**
     * Gets players last location.
     *
     * @param skip locations
     * @return last location
     */
    public Location getLastLocation(int skip) {
        return (lastLocations.isEmpty() || skip > lastLocations.size()) ? null : lastLocations.get(lastLocations.size() - skip);
    }

    /*
     * Homes
     */

    /**
     * Gets player home
     *
     * @param home name
     * @return location of the home
     */
    public PreLocation getHome(String home) {
        return homes.get(home);
    }

    /**
     * Sets player home to a location
     *
     * @param home name
     * @param loc  location
     */
    public void setHome(String home, Location loc) {
        setHome(home, new PreLocation(loc));
    }

    /**
     * Sets player home to a location
     *
     * @param loc location
     */
    public void setHome(String name, PreLocation loc) {
        homes.put(name, loc);
    }

    /**
     * Deletes player home
     *
     * @param home name
     */
    public void delHome(String home) {
        homes.remove(home);
    }

    /**
     * Checks if player has home by name
     *
     * @param home name
     * @return true if player has home with specific name
     */
    public boolean hasHome(String home) {
        return homes.containsKey(home);
    }

    /**
     * Returns homes as array
     *
     * @return homes array
     */
    public String[] getHomeArray() {
        return homes.keySet().toArray(new String[0]);
    }

    public boolean hasHomeSlots() {
        Player p = getPlayer();
        String permPrefix = HOME_AMOUNT_PERMISSION.asPermission();
        if (p.hasPermission(permPrefix + ".*")) {
            return true;
        }
        //sethome.4, homes 3
        for (PermissionAttachmentInfo attachmentInfo : p.getEffectivePermissions()) {
            if (attachmentInfo.getPermission().startsWith(permPrefix)) {
                String perm = attachmentInfo.getPermission();
                String end = perm.substring(perm.lastIndexOf('.')).replace(".", "");
                if (StringUtils.isNumeric(end) && Integer.parseInt(end) > homes.size()) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * Economy
     */

    //todo remove from oneuser? because now offlineusers require userargument with onlineplayer


    /**
     * Get balance of the user.
     *
     * @return amount of the money
     */
    public double getBalance() {
        return economy == null ? 0 : economy.getBalance(getPlayer());
    }

    /**
     * Sets money to user.
     *
     * @param amount of the money
     */
    public boolean setBalance(double amount) {
        if (economy.withdrawPlayer(offP, getBalance()).transactionSuccess()) {
            return economy.depositPlayer(offP, amount).transactionSuccess();
        }
        return false;
    }

    /**
     * Adds money to user.
     *
     * @param amount of the money
     * @return e
     */
    public boolean deposit(double amount) {
        return economy.depositPlayer(offP, amount).transactionSuccess();
    }

    /**
     * Removes money from user.
     *
     * @param amount of the money
     * @return result
     */
    public boolean withdraw(double amount) {
        return economy.withdrawPlayer(offP, amount).transactionSuccess();
    }
}