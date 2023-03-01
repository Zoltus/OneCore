package io.github.zoltus.onecore.listeners;

import io.github.zoltus.onecore.data.configuration.yamls.Config;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.zoltus.onecore.data.configuration.yamls.Config.CHAT_REMOVE_DUPLICATE_SPACES;
import static io.github.zoltus.onecore.data.configuration.yamls.Config.CHAT_TRIM;

public class ChatListener implements Listener {
    //Chat listening event
    @EventHandler
    public void asyncChatEvent(AsyncPlayerChatEvent e) {
        handleChatFormat(e);
        handleMentions(e);
    }

    private void handleMentions(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission(Config.MENTION_PERMISSION.asPermission())) {
            return;
        }
        String message = e.getMessage();
        Matcher matcher = Pattern.compile("@(\\w+)").matcher(message);
        while (matcher.find()) {
            Player target = Bukkit.getPlayer(matcher.group(1));
            int start = matcher.start();
            if (target != null /*&& !player.equals(sender)*/) {
                String beforeColor = ChatColor.getLastColors(message.substring(0, start));
                String continueColor = StringUtils.defaultIfEmpty(beforeColor, "§f");
                message = message.replace(matcher.group(),
                        Config.MENTION_COLOR.getString() + target.getDisplayName() + continueColor);
                target.playSound(target, Sound.valueOf(Config.MENTION_SOUND.get()), 1, 1);
                e.setMessage(message);
            }
        }
    }

    // String.format(format, this.player, this.message);
    private void handleChatFormat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        //Enables chat colors
        if (Config.CHAT_COLORS_ENABLED.getBoolean()
                && player.hasPermission(Config.CHAT_COLOR_PERMISSION.asPermission())) {
            e.setMessage(formatColors(e.getMessage()));
        }
        //Formats chat
        if (Config.CHAT_FORMATTER_ENABLED.getBoolean()) {
            String format = Config.CHAT_FORMAT.getString();
            format = format.replace("{0}", "%s");
            format = format.replace("{1}", "%s");
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                format = PlaceholderAPI.setPlaceholders(player, format);
            }
            if (CHAT_REMOVE_DUPLICATE_SPACES.getBoolean()) {
                format = format.replace("  ", " ");
            }
            if (CHAT_TRIM.getBoolean()) {
                format = format.trim();
            }
            //replaces %s with the player name and the message
            e.setFormat(formatColors(format));
        }
    }

    private String formatColors(String format) {
        MiniMessage mm = MiniMessage.builder().tags(StandardTags.defaults()).build();
        format = LegacyComponentSerializer.legacyAmpersand().serialize(mm.deserialize(format.replace("§", "&")));
        format = ChatColor.translateAlternateColorCodes('&', format);
        //Replaces invalid format characters if there are any leftovers
        return format;
    }
}



