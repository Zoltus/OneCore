package sh.zoltus.onecore.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static sh.zoltus.onecore.configuration.yamls.Config.CHAT_COLORS_ENABLED;
import static sh.zoltus.onecore.configuration.yamls.Config.CHAT_COLOR_PERMISSION;

public class ChatColors implements Listener {

    //todo register only if enabled
    @EventHandler(priority = EventPriority.MONITOR)
    public void chatMention(AsyncPlayerChatEvent e) {
        if (CHAT_COLORS_ENABLED.getBoolean()
            && e.getPlayer().hasPermission(CHAT_COLOR_PERMISSION.getAsPermission()))
            e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
    }
}
