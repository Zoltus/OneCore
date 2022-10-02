package sh.zoltus.onecore.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static sh.zoltus.onecore.data.configuration.yamls.Config.CHAT_COLOR_PERMISSION;

public class ChatColors implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void chatMention(AsyncPlayerChatEvent e) {
        if (e.getPlayer().hasPermission(CHAT_COLOR_PERMISSION.asPermission()))
            e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
    }
}
