package io.github.zoltus.onecore.listeners;

import io.github.zoltus.onecore.data.configuration.yamls.Config;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mentions implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void chatMention2(AsyncPlayerChatEvent e) {
        if (!e.getPlayer().hasPermission(Config.MENTION_PERMISSION.asPermission())) {
            return;
        }
        String orginal = e.getMessage();
        Matcher matcher = Pattern.compile("@(\\w+)").matcher(orginal);
        while (matcher.find()) {
            Player target = Bukkit.getPlayer(matcher.group(1));
            int start = matcher.start();
            if (target != null /*&& !player.equals(sender)*/) {
                e.setCancelled(true);
                String beforeColor = ChatColor.getLastColors(orginal.substring(0, start));
                String continueColor = StringUtils.defaultIfEmpty(beforeColor, "§f");
                String message = orginal.replace(matcher.group(),
                        Config.MENTION_COLOR.getString() + target.getDisplayName() + continueColor);
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (player.equals(target)) {
                        player.sendMessage(message);
                        target.playSound(target.getLocation(), Sound.valueOf(Config.MENTION_SOUND.get()), 1, 1);
                    } else {
                        player.sendMessage(orginal);
                    }
                });
            }
        }
    }
}
