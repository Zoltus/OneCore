package sh.zoltus.onecore;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import sh.zoltus.onecore.configuration.IConfig;
import sh.zoltus.onecore.configuration.yamls.Commands;
import sh.zoltus.onecore.listeners.*;
import sh.zoltus.onecore.listeners.tweaks.KickedForSpamming;
import sh.zoltus.onecore.listeners.tweaks.TeleportVelocity;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.commands.admin.*;
import sh.zoltus.onecore.player.command.commands.regular.*;
import sh.zoltus.onecore.player.teleporting.TeleportHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static sh.zoltus.onecore.configuration.yamls.Config.*;

public class Registerer {
    //For unregistering on disable to have better reload support
    private final List<Listener> listeners = new ArrayList<>();

    public static Registerer register(OneCore plugin) {
        Registerer registerer = plugin.getRegisterer();
        return registerer == null ? new Registerer(plugin) : registerer;
    }

    private void addIfEnabled(Listener listener, IConfig config) {
        if (config.getBoolean()) {
            listeners.add(listener);
        }
    }

    private Registerer(OneCore plugin) {
        addIfEnabled(new Mentions(), MENTIONS_ENABLED);
        addIfEnabled(new ChatColors(), CHAT_COLORS_ENABLED);
        addIfEnabled(new TeleportVelocity(), TELEPORT_VELOCITY_RESET);
        //Sign colors & ShiftEdit if enabled
        listeners.add(new SignColorHandler(plugin));
        if (Commands.INVSEE_ENABLED.getBoolean() || Commands.EnderChest_ENABLED.getBoolean()) {
            listeners.add(new InvSeeHandler());
        }
        listeners.addAll(List.of(
                new JoinHandler(),
                new KickedForSpamming(),
                new PlayerJumpEvent(),
                new QuitListener(),
                new TeleportHandler(),
                new TestListener()
        ));
        List<Class<? extends IOneCommand>> cmds = List.of(
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

        Bukkit.getConsoleSender().sendMessage("Registering command & listeners...");
        listeners.forEach(listener -> Bukkit.getServer().getPluginManager().registerEvents(listener, plugin));

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
                    } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}