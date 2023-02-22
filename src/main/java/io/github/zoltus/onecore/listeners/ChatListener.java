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

public class ChatListener implements Listener {
    //Chat listening event
    @EventHandler
    public void asyncChatEvent(AsyncPlayerChatEvent e) {
        handleChatFormat(e);
        handleMentions(e);
    }

    private void handleMentions(AsyncPlayerChatEvent e) {
        if (!e.getPlayer().hasPermission(Config.MENTION_PERMISSION.asPermission())) {
            return;
        }
        String formatted = String.format(e.getFormat(), e.getPlayer().getName(), e.getMessage());
        Matcher matcher = Pattern.compile("@(\\w+)").matcher(formatted);
        while (matcher.find()) {
            Player target = Bukkit.getPlayer(matcher.group(1));
            int start = matcher.start();
            if (target != null /*&& !player.equals(sender)*/) {
                e.setCancelled(true);
                String beforeColor = ChatColor.getLastColors(formatted.substring(0, start));
                String continueColor = StringUtils.defaultIfEmpty(beforeColor, "§f");
                String mentionMessage = formatted.replace(matcher.group(),
                        Config.MENTION_COLOR.getString() + target.getDisplayName() + continueColor);
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (player.equals(target)) {
                        player.sendMessage(mentionMessage);
                        target.playSound(target.getLocation(), Sound.valueOf(Config.MENTION_SOUND.get()), 1, 1);
                    } else {
                        player.sendMessage(formatted);
                    }
                });
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



