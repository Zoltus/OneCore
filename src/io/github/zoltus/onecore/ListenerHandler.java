package io.github.zoltus.onecore;

import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.github.zoltus.onecore.listeners.*;
import io.github.zoltus.onecore.listeners.tweaks.KickedForSpamming;
import io.github.zoltus.onecore.listeners.tweaks.TeleportVelocity;
import io.github.zoltus.onecore.player.teleporting.TeleportHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class ListenerHandler {
    private final List<Listener> listeners = new ArrayList<>();

    public static ListenerHandler register(OneCore plugin) {
        ListenerHandler handler = plugin.getListenerHandler();
        return handler == null ? new ListenerHandler(plugin) : handler;
    }

    public ListenerHandler(OneCore plugin) {
        plugin.getLogger().info("Registering listeners...");
        //Adds listeners to list if enabled and then registers them.
        addIfEnabled(new Mentions(), Config.MENTIONS_ENABLED.getBoolean());
        addIfEnabled(new ChatColors(), Config.CHAT_COLORS_ENABLED.getBoolean());
        addIfEnabled(new TeleportVelocity(), Config.TELEPORT_VELOCITY_RESET.getBoolean());
        addIfEnabled(new InvSeeHandler(), Commands.INVSEE_ENABLED.getBoolean() || Commands.EnderChest_ENABLED.getBoolean());
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
