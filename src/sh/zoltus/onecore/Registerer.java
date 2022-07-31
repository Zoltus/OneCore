package sh.zoltus.onecore;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import sh.zoltus.onecore.listeners.*;
import sh.zoltus.onecore.listeners.tweaks.KickedForSpamming;
import sh.zoltus.onecore.listeners.tweaks.TeleportVelocity;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.commands.admin.*;
import sh.zoltus.onecore.player.command.commands.regular.*;
import sh.zoltus.onecore.player.teleporting.TeleportHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import static sh.zoltus.onecore.configuration.yamls.Commands.EnderChest_ENABLED;
import static sh.zoltus.onecore.configuration.yamls.Commands.INVSEE_ENABLED;
import static sh.zoltus.onecore.configuration.yamls.Config.*;

public class Registerer {
    private final OneCore plugin;

    //listeners which will always be registered
    private final List<Listener> listeners = Arrays.asList(
            new JoinHandler(),
            new KickedForSpamming(),
            new PlayerJumpEvent(),
            new QuitListener(),
            new TeleportHandler(),
            new TestListener()
    );

    private final List<Class<? extends IOneCommand>> cmds = List.of(
            Back.class, Broadcast.class, ClearChat.class,
            DelHome.class, Feed.class,
            Fly.class, Gamemode.class, God.class,
            Heal.class, Home.class, EnderChest.class,
            Invsee.class, KillAll.class, Msg.class,
            Ping.class, PlayTime.class, Reload.class,
            Repair.class, Rtp.class, Seen.class,
            SetHome.class, SetMaxPlayers.class, SetSpawn.class,
            SetWarp.class, SignEdit.class, Spawn.class, Speed.class,
            SystemInfo.class, Tp.class, Tpa.class,
            Tpaccept.class, TpaHere.class, TpDeny.class,
            TpToggle.class, Time.class, Top.class,
            Warp.class, Weather.class
    );

    public static Registerer register(OneCore plugin) {
        Registerer registerer = plugin.getRegisterer();
        return registerer == null ? new Registerer(plugin) : registerer;
    }

    private Registerer(OneCore plugin) {
        this.plugin = plugin;
        Bukkit.getConsoleSender().sendMessage("Registering command & listeners...");
        registerListeneres();
        registerCommands();
    }

    private void registerListeneres() {
        //todo new instance only if enabled
        //Adds listeners only if config is enabled
        addIfEnabled(new Mentions(), MENTIONS_ENABLED.getBoolean());
        addIfEnabled(new ChatColors(), CHAT_COLORS_ENABLED.getBoolean());
        addIfEnabled(new TeleportVelocity(), TELEPORT_VELOCITY_RESET.getBoolean());
        //Sign colors & ShiftEdit if enabled
        addIfEnabled(new InvSeeHandler(), INVSEE_ENABLED.getBoolean() || EnderChest_ENABLED.getBoolean());
        listeners.add(new SignColorHandler(plugin));
        listeners.forEach(listener -> Bukkit.getServer().getPluginManager().registerEvents(listener, plugin));
    }

    private void registerCommands() {
        //Registers economy commands only if vault has been loaded successfully
        if (plugin.getVault() != null) {
            new Economy().register();
        }
        //Creates instanceof the class if its enabled and then registers it.
        //faster startup when disabling cmds
        cmds.stream().filter(IOneCommand::isEnabled)
                .forEach(aClass -> {
                    try {
                        IOneCommand iOneCommand = aClass.getDeclaredConstructor().newInstance();
                        iOneCommand.register();
                    } catch (InstantiationException | InvocationTargetException | IllegalAccessException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private void addIfEnabled(Listener listener, boolean bool) {
        if (bool) {
            listeners.add(listener);
        }
    }
}