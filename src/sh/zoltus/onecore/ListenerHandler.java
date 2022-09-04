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
    private final List<Listener> listeners = new ArrayList<>();

    public static ListenerHandler register(OneCore plugin) {
        ListenerHandler handler = plugin.getListenerHandler();
        return handler == null ? new ListenerHandler(plugin) : handler;
    }

    public ListenerHandler(OneCore plugin) {
        Bukkit.getConsoleSender().sendMessage("Registering listeners...");
        //Adds listeners to list if enabled and then registers them.
        addIfEnabled(new Mentions(), MENTIONS_ENABLED.getBoolean());
        addIfEnabled(new ChatColors(), CHAT_COLORS_ENABLED.getBoolean());
        addIfEnabled(new TeleportVelocity(), TELEPORT_VELOCITY_RESET.getBoolean());
        addIfEnabled(new InvSeeHandler(), INVSEE_ENABLED.getBoolean() || EnderChest_ENABLED.getBoolean());
        listeners.addAll(List.of(
                new SignListener(plugin),
                new JoinListener(plugin),
                new KickedForSpamming(),
                new KickedForSpamming(),
                new QuitListener(),
                new TeleportHandler(),
                new TestListener()));
        listeners.forEach(listener -> Bukkit.getServer().getPluginManager().registerEvents(listener, plugin));
    }

    private void addIfEnabled(Listener listener, boolean bool) {
        if (bool)
           listeners.add(listener);
    }
}
