package sh.zoltus.onecore;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.SoftDependency;
import org.bukkit.plugin.java.annotation.dependency.SoftDependsOn;
import org.bukkit.plugin.java.annotation.plugin.*;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import sh.zoltus.onecore.configuration.yamls.Commands;
import sh.zoltus.onecore.configuration.yamls.Config;
import sh.zoltus.onecore.configuration.yamls.Lang;
import sh.zoltus.onecore.database.Database;
import sh.zoltus.onecore.listeners.ConsoleFilter;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Stream;

@Plugin(name = "OneCore", version = "1.0-Beta")
@Description("A test plugin")
@Author("Zoltus")
@Website("https://www.spigotmc.org/members/zoltus.306747/")
@LogPrefix("OneCore")
@ApiVersion(ApiVersion.Target.v1_18)
//@Dependency("CommandAPI")
@SoftDependsOn({@SoftDependency("Vault")})
@LoadOrder(PluginLoadOrder.POSTWORLD)
@Getter
public class OneCore extends JavaPlugin implements Listener {

    @Getter
    private static OneCore plugin;
    private Economy vault;
    private Registerer registerer;

    //https://github.com/GeertBraakman/xLib/blob/master/src/main/java/io/github/geertbraakman/v0_3_4/api/command/APICommand.java
    //https://github.com/TheMode/CommandBuilder/tree/master/src/main/java/fr/themode/command
    @Override
    public void onLoad() {
        plugin = this;
        CommandAPI.onLoad(new CommandAPIConfig().verboseOutput(false));  //Loads commandapi
    }

    @Override
    public void onEnable() {//asddsfsdf
        CommandAPI.onEnable(this); //Loads commandapi
        long time = System.currentTimeMillis();
        Database.init(this); // Loads db & baltop todo only obj or static
        this.vault = hookEconomy();// Hooks economy if its enabled on config.
        this.registerer = Registerer.create(this);// Register listeners & Commands
        this.initMetrics(); // Inits metrics
        this.sendArt(); // Sends console art
        ConsoleFilter.init(); // Sets default config for all commands and settings if they are not set
        System.out.println("Successfully enabled. (" + (System.currentTimeMillis() - time) + "ms)");
    }

    @Override
    public void onDisable() {
        // Saves all users & settings on disable
        Database.database().saveAll();
        System.out.println("Saved users & settings to database...");
        registerer.unregisterCommands(); //Unregisters all cmds to better support reloading
    }

    private void initMetrics() {
        int pluginId = 12829;
        new Metrics(this, pluginId);
    }

    private void sendArt() {
        List.of("",
                // "§f                     o O O",
                "§9   ___  §x§5§5§9§f§f§f  ___      §f░§8  ____",
                "§9  / _ \\§x§5§5§9§f§f§f  / __|    §8][__|[]| §fAll in one train!",
                "§9 | (_) |§x§5§5§9§f§f§f| (__    §8{==§71.0§8==|_|‾‾‾‾‾|_|‾‾‾‾‾| ",
                "§9  \\___/§x§5§5§9§f§f§f  \\___|  §8.\\/o--000'‾'-0-0-'‾'-0-0-' ",
                ""
        ).forEach(line -> Bukkit.getConsoleSender().sendMessage(line));
    }

    private Economy hookEconomy() {
        Bukkit.getConsoleSender().sendMessage("Hooking economy...");
        if (Config.ECONOMY.getBoolean()) {
            if (getServer().getPluginManager().getPlugin("Vault") == null) {
                getLogger().log(Level.WARNING, "Vault not found, Economy features disabled.");
                return null;
            }

            if (Config.ECONOMY_USE_ONEECONOMY.getBoolean()) {
                getServer().getServicesManager().register(Economy.class, new sh.zoltus.onecore.OneEconomy(this), this, ServicePriority.Highest);
            }
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                Economy econ = rsp.getProvider();
                System.out.println("Economy hooked! (" + econ.getName() + ")");
                Database.database().loadEconomyAsync();
                return econ;
            }
        }
        return null;
    }

    private void testConfig() {
        Stream.of(Config.values()).filter(Objects::isNull).filter(obj2 -> false)
                .forEach(config -> System.out.println("§c" + config.name() + " is null!"));
        Stream.of(Commands.values()).filter(Objects::isNull).filter(obj1 -> false)
                .forEach(cmd -> System.out.println("§c" + cmd.name() + " is null!"));
        Stream.of(Lang.values()).filter(Objects::isNull).filter(obj -> false)
                .forEach(lang -> System.out.println("§c" + lang.name() + " is null!"));
        System.out.println("§aTested config");
    }
}
