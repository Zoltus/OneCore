package sh.zoltus.onecore.player.command;

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
import sh.zoltus.onecore.OneEconomy;
import sh.zoltus.onecore.player.command.commands.teleport.handlers.Request;
import sh.zoltus.onecore.player.command.commands.teleport.handlers.Teleport;
import sh.zoltus.onecore.utils.LocationUtils;
import sh.zoltus.onecore.utils.PreLocation;

import java.util.*;

import static sh.zoltus.onecore.configuration.yamls.Commands.HOME_AMOUNT_PERMISSION;
import static sh.zoltus.onecore.configuration.yamls.Config.START_MONEY;


@Data
public class User {

    private static OneCore plugin = OneCore.getPlugin();
    private static Economy economy = plugin.getVault();

    @Getter
    private static final Map<UUID, User> users = new HashMap<>();

    private transient final UUID uniqueId;
    private transient final OfflinePlayer offP;
    private transient final List<Location> lastLocations = new ArrayList<>();
    //tomap
    private transient final List<Request> requests = new ArrayList<>();
    //Database

    @Getter
    private boolean tpEnabled = false;
    //@SerializedName("homes")
    private Map<String, PreLocation> homes = new HashMap<>();

    public User(OfflinePlayer offP) {
        this.offP = offP;
        this.uniqueId = offP.getUniqueId();
        //Default settings:
        users.put(uniqueId, this);
        Bukkit.broadcastMessage("§cNew");
        //Sets balance to 0 if it doesnt exist, for toplist
        if (!OneEconomy.getBalances().containsKey(uniqueId)) {
            OneEconomy.getBalances().put(uniqueId, START_MONEY.getDouble());
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
    public static User of(Player p) {
        return users.get(p.getUniqueId());
    }


    /**
     * Used only for economy arg @ cmd.
     *
     * @param offP offlinePlayer
     * @return OneUser or null
     */
    public static User ofNullable(OfflinePlayer offP) {
        return users.getOrDefault(offP.getUniqueId(), null);
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
     * Settings
     */
    public void setTpEnabled(boolean bool) {
        this.tpEnabled = bool;
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
        String permPrefix = HOME_AMOUNT_PERMISSION.getAsPermission();
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

    /**
     * Get balance of the user.
     *
     * @return amount of the money
     */
//todo cleanup
    public double getBalance() {
        if (economy == null) {
            return 0;
        } else {
            return economy.getBalance(offP);
        }
    }

    /**
     * Sets money to user.
     *
     * @param amount of the money
     */
    public boolean setBalance(double amount) {
        if (economy != null) {
            if (economy.withdrawPlayer(offP, getBalance()).transactionSuccess()) {
                return economy.depositPlayer(offP, amount).transactionSuccess();
            }
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
        if (economy == null) {
            return false;
        } else {
            return economy.depositPlayer(offP, amount).transactionSuccess();
        }
    }

    /**
     * Removes money from user.
     *
     * @param amount of the money
     * @return result
     */
    public boolean withdraw(double amount) {
        if (economy == null) {
            return false;
        } else {
            return economy.withdrawPlayer(offP, amount).transactionSuccess();
        }
    }
}