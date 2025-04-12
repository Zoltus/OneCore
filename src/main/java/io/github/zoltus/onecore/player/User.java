package io.github.zoltus.onecore.player;

import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.economy.OneEconomy;
import io.github.zoltus.onecore.player.teleporting.DelayedTeleport;
import io.github.zoltus.onecore.player.teleporting.LocationUtils;
import io.github.zoltus.onecore.player.teleporting.Request;
import io.github.zoltus.onecore.utils.PreLocation;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.HOME_DEFAULT_NAME;
import static io.github.zoltus.onecore.data.configuration.yamls.Config.DEFAULT_HOME_AMOUNT;


@Data
public class User {

    @Getter
    private static final ConcurrentHashMap<UUID, User> users = new ConcurrentHashMap<>();
    private static OneCore plugin = OneCore.getPlugin();
    private static Economy economy = plugin.getVault();
    private final OfflinePlayer offP;
    @Getter
    @Setter
    private Location lastLocation;
    private final List<Request> requests = new ArrayList<>();

    private final UUID uniqueId;
    private ConcurrentHashMap<String, PreLocation> homes = new ConcurrentHashMap<>();
    private DelayedTeleport teleport;
    private boolean hasTpEnabled = true;
    private boolean isVanished = false;
    private boolean isGod = false;
    //private boolean isGod = false; todo god? or use mc?

    //todo onjoin set player object, on leave null? So then i could remove getPlayer() and isOnline() methods

    public User(OfflinePlayer offP) {
        this.offP = offP;
        this.uniqueId = offP.getUniqueId();
        //Default settings:
        users.put(uniqueId, this);
        //Bukkit.broadcastMessage("§cNew");
        //Sets balance to 0 if it doesnt exist, for toplist
        if (plugin.getVault() != null) {
            OneEconomy.getBalances().computeIfAbsent(uniqueId, k -> Config.START_MONEY.getDouble());
        }
    }

    /*
     OneUser -> visited -> map -> db -> new
     OneUser -> never -> null
     OneUser -> never -> new
     */

    @Nullable
    public static User of(OfflinePlayer offP) {
        return users.get(offP.getUniqueId());
    }

    @NotNull
    public static User of(Player p) {
        return users.get(p.getUniqueId());
    }

    @Nullable
    public static User of(UUID uuid) {
        return users.get(uuid);
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

    public void teleport(Object obj) {
        if (!isOnline()) {
            return;
        }
        Player p = getPlayer();
        if (teleport != null) {
            teleport.cancel(Lang.TP_CANCELLED_BY_NEW_TELE);
        }
        if (p.hasPermission(Config.TELEPORT_CD_BYPASS.asPermission())
                || p.getGameMode() == GameMode.CREATIVE
                || p.getGameMode() == GameMode.SPECTATOR) {
            Location loc = null;
            if (obj instanceof User target) {
                loc = target.getPlayer().getLocation(); //todo offline sup?
            } else if (obj instanceof Location location) {
                loc = location;
            }
            LocationUtils.teleportWMountSafeAsync(p, loc);
        } else {
            if (obj instanceof User target) {
                teleport = new DelayedTeleport(this, target);
            } else if (obj instanceof Location loc) {
                teleport = new DelayedTeleport(this, loc);
            }
        }
    }

    public void setVanished(boolean vanished) {
        this.isVanished = vanished;
    }

    /*
     * Homes
     */

    /**
     * Gets player home, if home is not found, returns default home
     *
     * @param home name
     * @return location of the home
     */
    public PreLocation getorDefaultHOme(String home) {
        String defaultHome = HOME_DEFAULT_NAME.get();
        return homes.get(home.toLowerCase()) != null ? homes.get(home.toLowerCase()) : homes.get(defaultHome);
    }

    /**
     * Sets player home to a location
     *
     * @param home name
     * @param loc  location
     */
    public void setHome(String home, Location loc) {
        PreLocation updatedHome = homes.getOrDefault(home.toLowerCase(), new PreLocation(loc));
        setHome(home.toLowerCase(), updatedHome);
    }

    /**
     * Sets player home to a location
     *
     * @param loc location
     */
    public void setHome(String name, PreLocation loc) {
        homes.put(name.toLowerCase(), loc);
    }

    /**
     * Deletes player home
     *
     * @param home name
     */
    public void delHome(String home) {
        homes.remove(home.toLowerCase());
    }

    /**
     * Checks if player has home by name
     *
     * @param home name
     * @return true if player has home with specific name
     */
    public boolean hasHome(String home) {
        //todo needs to check also if home is "toDelete"
        return homes.containsKey(home.toLowerCase());
    }

    /**
     * Returns homes as array
     *
     * @return homes array
     */
    public String[] getHomeArray() {
        return homes.keySet().toArray(new String[0]);
    }

    public boolean hasFreeHomeSlot() {
        String perm = Commands.SETHOME_AMOUNT_PERMISSION.asPermission() + ".";
        Player player = getPlayer();
        if (player.hasPermission("*") || getPlayer().hasPermission(perm + "*")) {
            return true;
        }
        return player.getEffectivePermissions().stream()
                .filter(permission -> permission.getPermission().startsWith(perm))
                .map(permission -> Integer.parseInt(permission.getPermission().replace(perm, "")))
                .max(Integer::compareTo)
                .orElse(DEFAULT_HOME_AMOUNT.getInt()) > homes.size();
    }

    /*
     * Economy
     */

    /**
     * Get balance of the user.
     *
     * @return amount of the money
     */
    public double getBalance() {
        return economy == null ? 0 : economy.getBalance(offP);
    }

    /**
     * Sets money to user.
     *
     * @param amount of the money
     */
    public boolean setBalance(double amount) {
        if (economy != null && economy.withdrawPlayer(offP, getBalance()).transactionSuccess()) {
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
        if (economy == null) {
            return false;
        }
        return economy.depositPlayer(offP, amount).transactionSuccess();
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
        }
        return economy.withdrawPlayer(offP, amount).transactionSuccess();
    }
}