package sh.zoltus.onecore;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import sh.zoltus.onecore.listeners.*;
import sh.zoltus.onecore.listeners.tweaks.KickedForSpamming;
import sh.zoltus.onecore.listeners.tweaks.TeleportVelocity;
import sh.zoltus.onecore.player.teleporting.TeleportHandler;

import java.util.ArrayList;
import java.util.List;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.EnderChest_ENABLED;
import static sh.zoltus.onecore.data.configuration.yamls.Commands.INVSEE_ENABLED;
import static sh.zoltus.onecore.data.configuration.yamls.Config.*;

public class ListenerHandler {
    private final OneCore plugin;

    public ListenerHandler(OneCore plugin) {
        this.plugin = plugin;
        Bukkit.getConsoleSender().sendMessage("Registering listeners...");
    }

    public static ListenerHandler register(OneCore plugin) {
        ListenerHandler handler = plugin.getListenerHandler();
        return handler == null ? new ListenerHandler(plugin) : handler;
    }

    /**
     Cmd&Listener
     Cmd&Listener(plugin)
     Cmd
     Cmd
     Listener
     Listener(Plugin)
     */

    //implemensts registerable?
    //Listeners which will always be registered
    private final List<Listener> listeners = new ArrayList<>(List.of(
            new KickedForSpamming(),
            new PlayerJumpEvent(),
            new QuitListener(),
            new TeleportHandler(),
            new TestListener()
    ));
    //todo cleanup
    private void registerListeners() {
        //Adds listeners only if config is enabled
        addIfEnabled(new Mentions(), MENTIONS_ENABLED.getBoolean());
        addIfEnabled(new ChatColors(), CHAT_COLORS_ENABLED.getBoolean());
        addIfEnabled(new TeleportVelocity(), TELEPORT_VELOCITY_RESET.getBoolean());
        //Sign colors & ShiftEdit if enabled
        addIfEnabled(new InvSeeHandler(), INVSEE_ENABLED.getBoolean() || EnderChest_ENABLED.getBoolean());
        listeners.add(new JoinListener(plugin));
        listeners.add(new SignListener(plugin));
        listeners.forEach(listener -> Bukkit.getServer().getPluginManager().registerEvents(listener, plugin));
    }

    private void addIfEnabled(Listener listener, boolean bool) {
        if (bool) {
            listeners.add(listener);
        }
    }
}
