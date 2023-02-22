package io.github.zoltus.onecore.economy;

import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

import java.util.logging.Level;

public class EconomyHandler {

    public static Economy hook(OneCore plugin) {
        if (!Config.ECONOMY_HOOK.getBoolean()) {
            return null;
        }
        plugin.getLogger().info("Hooking economy...");
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().log(Level.WARNING, "Vault not found, Economy features disabled.");
            return null;
        } else {
            Economy vault = plugin.getVault();
            if (vault != null) {
                plugin.getLogger().info("Economy is already hooked");
                return vault;
            } else {
                if (Config.ECONOMY_USE_ONEECONOMY.getBoolean()) {
                    plugin.getServer().getServicesManager().register(Economy.class, new OneEconomy(plugin), plugin, ServicePriority.Highest);
                }
                RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
                if (rsp != null) {
                    vault = rsp.getProvider();
                    plugin.getLogger().info("Economy hooked! (" + vault.getName() + ")");
                    return vault;
                }
            }
        }
        return null;
    }

}
