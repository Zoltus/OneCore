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
        String msgg = e.getMessage();
        Matcher matcher = Pattern.compile("@([A-Za-z0-9_]+)")
                .matcher(e.getMessage());
        while (matcher.find()) {
            Player player = Bukkit.getPlayer(matcher.group(1));
            int start = matcher.start();
            if (player != null /*&& !player.equals(sender)*/) {
                e.setCancelled(true);

                String beforeColor = ChatColor.getLastColors(msgg.substring(0, start));
                String continueColor = StringUtils.defaultIfEmpty(beforeColor, "§f");
                e.setMessage(e.getMessage().replace(matcher.group(),
                        MENTION_COLORS + player.getDisplayName() + continueColor));
                player.playSound(player.getLocation(), Sound.valueOf(Lang.MENTION_SOUND.get()), 1, 1);
            }
        }
    }
}

    /*@EventHandler(priority = EventPriority.MONITOR)
    public void chatMention(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String msgg = e.getMessage();
        if (p.hasPermission(MENTION_PERMISSION.asPermission()) && msgg.contains(MENTION_TAG)) {
            StringBuilder sb = new StringBuilder();
            for (Player target : Bukkit.getOnlinePlayers()) {//todo use this method on chatbuilder
                String name = target.getName();
                String pattern = "(?i)" + MENTION_TAG + name;
                Matcher match = Pattern.compile(pattern).matcher(msgg);
                while (match.find()) {
                    int start = match.start();
                    String beforeColor = ChatColor.getLastColors(msgg.substring(0, start));
                    String continueColor = StringUtils.defaultIfEmpty(beforeColor, "§f");
                    match.appendReplacement(sb, MENTION_COLORS + name + continueColor);
                }
                match.appendTail(sb);
            }
            e.setMessage(sb.toString());
        }
    }*/
