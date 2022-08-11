package sh.zoltus.onecore.listeners;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static sh.zoltus.onecore.data.configuration.yamls.Lang.*;

public class Mentions implements Listener {
    private final String mentionColors = MENTION_COLOR.getString(); //
    private final String mentionTag = MENTION_TAG.getString();

    @EventHandler(priority = EventPriority.MONITOR)
    public void chatMention(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String msgg = e.getMessage();
        if (p.hasPermission(MENTION_PERMISSION.getAsPermission()) && msgg.contains(mentionTag)) {
            StringBuilder sb = new StringBuilder();
            for (Player target : Bukkit.getOnlinePlayers()) {//todo use this method on chatbuilder
                String name = target.getName();
                String pattern = "(?i)" + mentionTag + name;
                Matcher match = Pattern.compile(pattern).matcher(msgg);
                while (match.find()) {
                    int start = match.start();
                    String beforeColor = ChatColor.getLastColors(msgg.substring(0, start));
                    String continueColor = StringUtils.defaultIfEmpty(beforeColor, "§f");
                    match.appendReplacement(sb, mentionColors + name + continueColor);
                }
                match.appendTail(sb);
            }
            e.setMessage(sb.toString());
        }
    }
}
