package io.github.zoltus.onecore;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import io.github.zoltus.onecore.data.BackupHandler;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.github.zoltus.onecore.data.database.Database;
import io.github.zoltus.onecore.data.database.SQLiteImpl;
import io.github.zoltus.onecore.economy.EconomyHandler;
import io.github.zoltus.onecore.listeners.*;
import io.github.zoltus.onecore.listeners.tweaks.KickedForSpamming;
import io.github.zoltus.onecore.listeners.tweaks.TeleportVelocity;
import io.github.zoltus.onecore.placeholders.PapiExpansion;
import io.github.zoltus.onecore.player.teleporting.TeleportHandler;
import io.github.zoltus.onecore.worldguard.WGFlags;
import io.github.zoltus.onecore.worldguard.WGTabComplete;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Getter
public final class OneCore extends JavaPlugin {
    //Jenkins test
    @Getter
    private static OneCore plugin;
    private Economy vault;
    private Database database;
    private BackupHandler backupHandler;
    private CommandHandler commandHandler;
    private BukkitAudiences adventure;
    private ConsoleFilter consoleFilter;
    private WGFlags worldGuardFlags;

    @Override
    public void onLoad() {
        plugin = this;
        //Loads commandapi
        // Load the CommandAPI
        CommandAPI.onLoad(
                // Configure the CommandAPI
                new CommandAPIBukkitConfig(this)
                        // Turn on verbose output for command registration logs
                        .verboseOutput(false)
                        // Give file where Brigadier's command registration tree should be dumped
                        //.dispatcherFile(new File(getDataFolder(), "command_registration.json"))
                        // Point to the NBT API we want to use
                        .initializeNBTAPI(NBTContainer.class, NBTContainer::new));

        Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if (worldGuard == null) {
            plugin.getLogger().log(Level.WARNING, "WorldGuard not found, WorldGuard Extra Flags disabled.");
        } else {
            this.worldGuardFlags = new WGFlags((WorldGuardPlugin) worldGuard);
        }
    }

    @Override
    public void onEnable() {
        //Enabled adventure support for bukkit
        this.adventure = BukkitAudiences.create(this);
        //Loads CommandAPI
        CommandAPI.onEnable();
        //long time = System.currentTimeMillis();
        // Loads db & baltop todo only obj
        this.database = SQLiteImpl.init(this);
        // Hooks economy if its enabled on config. todo, if enabled conomy and no vault, throws error
        this.vault = EconomyHandler.hook(this);
        //Registers Commands if enabled. Needs to be before listeners.
        this.commandHandler = CommandHandler.register(this);
        // Inits metrics to bstats
        new Metrics(this, 12829);
        // Starts caching users
        this.consoleFilter = ConsoleFilter.init();
        this.database.loadData();
        //todo mayby remove, creates user for new users, supports if loaded mid server
        JoinListener.loadOnlinePlayers();
        this.backupHandler = new BackupHandler(this); // Initializes backup handler //todo reenable
        if (worldGuardFlags != null) {
            this.worldGuardFlags.onEnable();
        }
        //Registers Listeners
        registerListeners();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PapiExpansion().register();
        }
        sendArt();
        //plugin.getLogger().info("Successfully enabled. (" + (System.currentTimeMillis() - time) + "ms)");
    }

    @Override
    public void onDisable() {
        // Saves all users & settings on disable
        database.saveData(false);
        plugin.getLogger().info("Saved users & settings to database...");
        //Unregisters all cmds on unload. Trying to support reloading plugin.
        CommandAPI.getRegisteredCommands().forEach(cmd -> CommandAPI.unregister(cmd.commandName(), true));
        //Disables commandapia
        CommandAPI.onDisable();
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        // Closes database connection
        database.closeConnection();
    }

    public BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    private void sendArt() {
        Bukkit.getConsoleSender().sendMessage("""
                
                §f                    o O O
                §9   ___  §x§5§5§9§f§f§f  ___      §f░§8  ____
                §9  / _ \\§x§5§5§9§f§f§f  / __|    §8][__|[]| §7All in one train!
                §9 | (_) |§x§5§5§9§f§f§f| (__    §8{=======|_|‾‾‾‾‾|_|‾‾‾‾‾|
                §9  \\___/§x§5§5§9§f§f§f  \\___|  §8.\\/o--000'‾'-0-0-'‾'-0-0-'
                """);
        Bukkit.getConsoleSender().sendMessage(" ");
    }

    private void registerListeners() {
        plugin.getLogger().info("Registering listeners...");
        //Adds listeners to list if enabled and then registers them.
        List<Listener> list = new ArrayList<>();
        if (Commands.INVSEE_ENABLED.getBoolean() || Commands.ENDER_CHEST_ENABLED.getBoolean())
            list.add(new InvSeeHandler());
        if (Config.TELEPORT_VELOCITY_RESET.getBoolean())
            list.add(new TeleportVelocity());
        if (worldGuardFlags != null)
            list.add(worldGuardFlags);
        list.addAll(List.of(
                new ChatListener(),
                new SignListener(plugin),
                new JoinListener(plugin),
                new KickedForSpamming(),
                new QuitListener(),
                new TeleportHandler(),
                new WGTabComplete()));
        list.forEach(listener -> Bukkit.getServer().getPluginManager().registerEvents(listener, plugin));
    }
}

