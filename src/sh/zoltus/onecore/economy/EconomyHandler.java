package sh.zoltus.onecore.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import sh.zoltus.onecore.OneCore;
import sh.zoltus.onecore.configuration.yamls.Config;
import sh.zoltus.onecore.database.Database;

import java.util.logging.Level;

public class EconomyHandler {
    public static Economy hook(OneCore plugin) {
        if (!Config.ECONOMY.getBoolean()) {
            return null;
        }
        Bukkit.getConsoleSender().sendMessage("Hooking economy...");
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().log(Level.WARNING, "Vault not found, Economy features disabled.");
            return null;
        } else {
            Economy vault = plugin.getVault();
            if (vault != null) {
                Bukkit.getConsoleSender().sendMessage("Economy is already hooked");
                return vault;
            } else {
                if (Config.ECONOMY_USE_ONEECONOMY.getBoolean()) {
                    plugin.getServer().getServicesManager().register(Economy.class, new OneEconomy(plugin), plugin, ServicePriority.Highest);
                }
                RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
                if (rsp != null) {
                    vault = rsp.getProvider();
                    Bukkit.getConsoleSender().sendMessage("Economy hooked! (" + vault.getName() + ")");
                    //todo to load eco perplayer not whole eco?
                    Database.database().loadEconomyAsync();
                    return vault;
                }
            }
        }
        return null;
    }
}
