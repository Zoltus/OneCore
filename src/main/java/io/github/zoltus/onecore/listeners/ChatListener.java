package io.github.zoltus.onecore.listeners;

import io.github.zoltus.onecore.data.configuration.yamls.Config;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer lcs = LegacyComponentSerializer.legacySection();

    //Chat listening event
    @EventHandler
    public void asyncChatEvent(AsyncPlayerChatEvent e) {
        handleChatFormat(e);
        handleMentions(e);
    }

    //todo @everyone bugs a bit with normal mentions if combines "hi@everyone a dd@Zoltus abb"
    private void handleMentions(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission(Config.MENTION_PERMISSION.asPermission())) {
            return;
        }
        String message = e.getMessage();
        //Handle @<player>
        Matcher matcher = Pattern.compile("@(\\w+)|@everyone").matcher(message);
        while (matcher.find()) {
            int start = matcher.start();
            String beforeColor = ChatColor.getLastColors(message.substring(0, start));
            String continueColor = StringUtils.defaultIfEmpty(beforeColor, "§f");
            //Handle @everyone
            if (matcher.group().equals("@everyone")) {
                if (p.hasPermission(Config.MENTION_EVERYONE_PERMISSION.asPermission())) {
                    for (Player target : Bukkit.getOnlinePlayers()) {
                        message = message.replace(matcher.group(), Config.MENTION_COLOR.getString()
                                + "@Everyone" + continueColor);
                        target.playSound(target, Sound.valueOf(Config.MENTION_SOUND.get()), 1, 1);
                    }
                }
            } else {
                Player target = Bukkit.getPlayer(matcher.group(1));
                if (target != null /*&& !player.equals(sender)*/) {
                    message = message.replace(matcher.group(), Config.MENTION_COLOR.getString()
                            + target.getDisplayName() + continueColor);
                    target.playSound(target, Sound.valueOf(Config.MENTION_SOUND.get()), 1, 1);
                }
            }
        }
        String colorFormatted = translateColors(message);
        e.setMessage(colorFormatted);
    }

    //todo better errorcatch, doesnt work properly
    // String.format(format, this.player, this.message);
    private void handleChatFormat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        //Enables chat colors if player has permission
        if (Config.CHAT_COLORS_ENABLED.getBoolean()
                && player.hasPermission(Config.CHAT_COLOR_PERMISSION.asPermission())) {
            e.setMessage(translateColors(e.getMessage()));
        }
        //Formats chat
        if (Config.CHAT_FORMATTER_ENABLED.getBoolean()) {
            String format = Config.CHAT_FORMAT.getString();
            format = format.replace("{0}", "%s");
            format = format.replace("{1}", "%s");
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                format = PlaceholderAPI.setBracketPlaceholders(player, format);
            }
            //TagResolver.resolver("myhover", Tag.styling(ComponentBuilder)); // will display your custom text as hover
            Placeholder.component("name", Component.text("TEST", NamedTextColor.RED));

            if (CHAT_REMOVE_DUPLICATE_SPACES.getBoolean()) {
                format = format.replace("  ", " ");
            }
            if (CHAT_TRIM.getBoolean()) {
                format = format.trim();
            }
            //Translates colors for chat format
            format = translateColors(format);
            try {
                e.setFormat(format);
            } catch (Exception ex) {
                System.out.println("Error while formatting chat message! " + ex.getMessage());
            }
        }
    }

    public static String translateColors(String str) {
        str = lcs.serialize(mm.deserialize(str.replace("§", "&")));
        str = ChatColor.translateAlternateColorCodes('&', str);
        return str;
    }
}



