package io.github.zoltus.onecore;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import io.github.zoltus.onecore.data.BackupHandler;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.data.database.Database;
import io.github.zoltus.onecore.economy.EconomyHandler;
import io.github.zoltus.onecore.listeners.ConsoleFilter;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.Libraries;
import org.bukkit.plugin.java.annotation.dependency.Library;
import org.bukkit.plugin.java.annotation.dependency.SoftDependency;
import org.bukkit.plugin.java.annotation.dependency.SoftDependsOn;
import org.bukkit.plugin.java.annotation.plugin.*;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Plugin(name = "OneCore", version = "1.0-Beta")
@Description("A test plugin")
@Author("Zoltus")
@Website("https://www.spigotmc.org/members/zoltus.306747/")
@LogPrefix("OneCore")
@ApiVersion(ApiVersion.Target.v1_19)
@SoftDependsOn({@SoftDependency("Vault")})
@LoadOrder(PluginLoadOrder.POSTWORLD)
@Libraries({
        @Library("org.bstats:bstats-bukkit:3.0.0"),
        @Library("org.apache.commons:commons-compress:1.21"),
        @Library("org.apache.logging.log4j:log4j-core:2.18.0"),
        @Library("dev.jorel:commandapi-shade:8.7.1")
})
@Getter
public final class OneCore extends JavaPlugin implements Listener {
    @Getter
    private static OneCore plugin;

    private Economy vault;
    private Database database;
    private BackupHandler backupHandler;
    private CommandHandler commandHandler;
    private ListenerHandler listenerHandler;

    @Override
    public void onLoad() {
        plugin = this;
        CommandAPI.onLoad(new CommandAPIConfig().verboseOutput(false));  //Loads commandapi
    }

    //todo backup interval to config
    @Override
    public void onEnable() {
        CommandAPI.onEnable(this); //Loads commandapi
        long time = System.currentTimeMillis();
        this.database = Database.init(this); // Loads db & baltop todo only obj
        this.vault = EconomyHandler.hook(this);// Hooks economy if its enabled on config.
        this.listenerHandler = ListenerHandler.register(this); //Registers listeners if enabled
        this.commandHandler = CommandHandler.register(this); //Registers Commands if enabled
        new Metrics(this, 12829); // Inits metrics to bstats
        ConsoleFilter.init(); // Sets default config for all commands and settings if they are not set
        testConfig(); // Tests config for missing values
        this.database.cacheUsers();
        this.backupHandler = new BackupHandler(this); // Initializes backup handler
        this.backupHandler.start(); //todo to singleton
        //Starts caching users
        plugin.getLogger().info("Successfully enabled. (" + (System.currentTimeMillis() - time) + "ms)");
        sendArt(); // Sends art with 1 tick delay so the art will be sent after the server has been fully loaded.
    }

    @Override
    public void onDisable() {
        // Saves all users & settings on disable
        database.saveUsers();
        plugin.getLogger().info("Saved users & settings to database...");
        CommandAPI.onDisable(); //Disables commandapi, unhooks chatpreviews
    }

    /**
     * Sends art with 1 tick delay so the art will be sent after the server has been fully loaded.
     */
    private void sendArt() {
        Bukkit.getScheduler().runTaskLater(this, () -> List.of(
                "",
                "§f                    o O O",
                "§9   ___  §x§5§5§9§f§f§f  ___      §f░§8  ____",
                "§9  / _ \\§x§5§5§9§f§f§f  / __|    §8][__|[]| §7All in one train!",
                "§9 | (_) |§x§5§5§9§f§f§f| (__    §8{==§71.0§8==|_|‾‾‾‾‾|_|‾‾‾‾‾| ",
                "§9  \\___/§x§5§5§9§f§f§f  \\___|  §8.\\/o--000'‾'-0-0-'‾'-0-0-' ",
                ""
        ).forEach(line -> Bukkit.getConsoleSender().sendMessage(line)), 1);
    }

    private void testConfig() {
        Stream.of(Config.values()).filter(Objects::isNull).filter(obj2 -> false)
                .forEach(config -> plugin.getLogger().info("§c" + config.name() + " is null!"));
        Stream.of(Commands.values()).filter(Objects::isNull).filter(obj1 -> false)
                .forEach(cmd -> plugin.getLogger().info("§c" + cmd.name() + " is null!"));
        Stream.of(Lang.values()).filter(Objects::isNull).filter(obj -> false)
                .forEach(lang -> plugin.getLogger().info("§c" + lang.name() + " is null!"));
        plugin.getLogger().info("§aTested config");
    }
}

