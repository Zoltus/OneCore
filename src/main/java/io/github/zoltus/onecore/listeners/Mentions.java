package io.github.zoltus.onecore.listeners;

import io.github.zoltus.onecore.data.configuration.yamls.Lang;
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

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.MENTION_COLOR;


public class Mentions implements Listener {

    private final String MENTION_COLORS = MENTION_COLOR.getString();

    @EventHandler(priority = EventPriority.MONITOR)
    public void chatMention2(AsyncPlayerChatEvent e) {
        String orgMsg = e.getMessage();
        Matcher matcher = Pattern.compile("@(\\w+)")
                .matcher(e.getMessage());
        while (matcher.find()) {
            Player target = Bukkit.getPlayer(matcher.group(1));
            int start = matcher.start();
            if (target != null /*&& !player.equals(sender)*/) {
                e.setCancelled(true);
                String beforeColor = ChatColor.getLastColors(orgMsg.substring(0, start));
                String continueColor = StringUtils.defaultIfEmpty(beforeColor, "§f");
                String message = e.getMessage().replace(matcher.group(),
                        MENTION_COLORS + target.getDisplayName() + continueColor);
                e.setMessage(message);
                target.playSound(target.getLocation(), Sound.valueOf(Lang.MENTION_SOUND.get()), 1, 1);
            }
        }
    }
}
