package io.github.zoltus.onecore;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import io.github.zoltus.onecore.data.BackupHandler;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.data.database.Database;
import io.github.zoltus.onecore.economy.EconomyHandler;
import io.github.zoltus.onecore.listeners.*;
import io.github.zoltus.onecore.listeners.tweaks.KickedForSpamming;
import io.github.zoltus.onecore.listeners.tweaks.TeleportVelocity;
import io.github.zoltus.onecore.player.teleporting.TeleportHandler;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.java.annotation.dependency.Libraries;
import org.bukkit.plugin.java.annotation.dependency.Library;
import org.bukkit.plugin.java.annotation.dependency.SoftDependency;
import org.bukkit.plugin.java.annotation.dependency.SoftDependsOn;
import org.bukkit.plugin.java.annotation.plugin.*;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Plugin(name = "MotiCore", version = "1.0-Beta")
@Description("Core plugin for motimaa.")
@Author("Zoltus")
@Website("https://www.spigotmc.org/members/zoltus.306747/")
@LogPrefix("MotiCore")
@ApiVersion(ApiVersion.Target.v1_19)
@LoadOrder(PluginLoadOrder.POSTWORLD)
@SoftDependsOn({
        @SoftDependency("Vault"),
        @SoftDependency("PlaceholderAPI")
})
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

    //Unit testing
    public OneCore() {
        super();
    }

    //Unit testing
    protected OneCore(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onLoad() {
        plugin = this;
        //Loads commandapi
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(false));
    }

    //todo backup interval to config
    @Override
    public void onEnable() {
        //Loads commandapi
        CommandAPI.onEnable();
        long time = System.currentTimeMillis();
        // Loads db & baltop todo only obj
        this.database = Database.init(this);
        // Hooks economy if its enabled on config.
        this.vault = EconomyHandler.hook(this);
        //Registers Commands if enabled. Needs to be before listeners.
        this.commandHandler = CommandHandler.register(this);
        //Registers Listeners
        registerListeners();
        // Inits metrics to bstats
        new Metrics(this, 12829); //todo fix for test
        // Sets default config for all commands and settings if they are not set
        ConsoleFilter.init();
        this.database.cacheUsers();
        //todo load all online players aswell to support loading mid-game
        JoinListener.loadOnlinePlayers();
        //todo cleanup
        this.backupHandler = new BackupHandler(this); // Initializes backup handler //todo reenable
        this.backupHandler.start();
        //Starts caching users
        plugin.getLogger().info("Successfully enabled. (" + (System.currentTimeMillis() - time) + "ms)");
        sendArt(); // Sends art with 1 tick delay so the art will be sent after the server has been fully loaded.
        // Tests config for missing values
        //testConfig();
    }

    @Override
    public void onDisable() {
        // Saves all users & settings on disable
        database.saveUsers();
        plugin.getLogger().info("Saved users & settings to database...");
        //Unregisters all cmds on unload. Trying to support reloading plugin.
        CommandAPI.getRegisteredCommands().forEach(cmd -> CommandAPI.unregister(cmd.commandName(), true));
        //Disables commandapi, unhooks chatpreviews //todo old? is it needed check docs
        CommandAPI.onDisable();
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

    private void registerListeners() {
        plugin.getLogger().info("Registering listeners...");
        //Adds listeners to list if enabled and then registers them.
        List<Listener> list = new ArrayList<>();
        if (Commands.INVSEE_ENABLED.getBoolean() || Commands.ENDER_CHEST_ENABLED.getBoolean())
            list.add(new InvSeeHandler());
        if (Config.MENTIONS_ENABLED.getBoolean())
            list.add(new Mentions());
        if (Config.TELEPORT_VELOCITY_RESET.getBoolean())
            list.add(new TeleportVelocity());
        list.addAll(List.of(
                new ChatFormatter(),
                new SignListener(plugin),
                new JoinListener(plugin),
                new KickedForSpamming(),
                new QuitListener(),
                new TeleportHandler(),
                new TestListener()));
        list.forEach(listener -> Bukkit.getServer().getPluginManager().registerEvents(listener, plugin));
    }

    private void testConfig() {
        Stream.of(Config.values(), Commands.values(), Lang.values())
                .flatMap(Arrays::stream)
                .filter(val -> val.get() == null)
                .forEach(val -> plugin.getLogger().warning("§cNull value: " + val.name() + ": " + val.getPath()
                        + " Please report this to the developer!"));
        plugin.getLogger().info("§aTested config");
    }
}

