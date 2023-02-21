package io.github.zoltus.onecore;

import io.github.zoltus.onecore.data.configuration.yamls.Config;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.IllegalFormatException;
import java.util.regex.Pattern;

public class ChatFormatter implements Listener {
    //Chat listening event
    @EventHandler
    public void asyncChatEvent(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        MiniMessage minimessage = MiniMessage.builder().tags(StandardTags.defaults()).build();

        //Enables chat colors
        if (Config.CHAT_COLORS_ENABLED.getBoolean()
                && player.hasPermission(Config.CHAT_COLOR_PERMISSION.asPermission())) {

            String legacy = LegacyComponentSerializer.legacyAmpersand().serialize(minimessage .deserialize(e.getMessage()));

            e.setMessage(ChatColor.translateAlternateColorCodes('&', legacy));
        }
        //Formats chat
        if (Config.CHAT_FORMATTER_ENABLED.getBoolean()) {
            String format = Config.CHAT_FORMAT.getString();
            format = format.replace("{0}", "%s");
            format = format.replace("{1}", "%s");
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                format = PlaceholderAPI.setPlaceholders(player, format);
            }
            try {
                format = LegacyComponentSerializer.legacyAmpersand().serialize(minimessage .deserialize(format.replace("§", "&")));
                e.setFormat(ChatColor.translateAlternateColorCodes('&', format));
            } catch (IllegalFormatException ex) {
                Bukkit.getLogger().warning("Chat format is invalid! " +
                        "You might be using broken placeholders which contain %!"
                        + " Format: " + Pattern.quote(format));
            }
        }
    }
}

