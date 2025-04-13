package io.github.zoltus.onecore.listeners;

import io.github.zoltus.onecore.data.configuration.LangBuilder;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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

import static io.github.zoltus.onecore.data.configuration.yamls.Config.*;

public class ChatListener implements Listener {
    //Chat listening event
    @EventHandler
    public void asyncChatEvent(AsyncPlayerChatEvent e) {
        handleChatFormat(e);
        if (MENTIONS_ENABLED.getBoolean()) {
            handleMentions(e);
        }
    }

    //todo @everyone bugs a bit with normal mentions if combines "hi@everyone a dd@Zoltus abb"
    private void handleMentions(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission(Config.MENTION_PERMISSION.asPermission())) {
            return;
        }
        String message = e.getMessage();
        //Handle @<player>
        Matcher matcher = Pattern.compile("(\\w+)").matcher(message);
        while (matcher.find()) {
            int start = matcher.start();
            String beforeColor = ChatColor.getLastColors(message.substring(0, start));
            String continueColor = StringUtils.defaultIfEmpty(beforeColor, "<reset>"); // todo <reset>?
            Player target = Bukkit.getPlayer(matcher.group(1));
            if (target != null /*&& !player.equals(sender)*/) {
                message = message.replace(matcher.group(), Config.MENTION_COLOR.get()
                        + target.getDisplayName() + continueColor);

                if (Config.MENTION_SOUND_ENABLED.getBoolean()) {
                    Sound sound = Sound.valueOf(Config.MENTION_SOUND.get());
                    float volume = Config.MENTION_SOUND_VOLUME.getFloat();
                    float pitch = Config.MENTION_SOUND_PITCH.getFloat();
                    target.playSound(target, sound, volume, pitch);
                }
            }
        }
        LangBuilder builder = new LangBuilder(message);
        String colorFormatted = builder.buildLegacyString();
        e.setMessage(colorFormatted);
    }

    //todo better errorcatch, doesnt work properly
    // String.format(format, this.player, this.message);
    private void handleChatFormat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        //Enables chat colors if player has permission
        if (Config.CHAT_COLORS_ENABLED.getBoolean()
                && player.hasPermission(Config.CHAT_COLOR_PERMISSION.asPermission())) {
            LangBuilder builder = new LangBuilder(e.getMessage());
            String legacy = builder.buildLegacyString();
            e.setMessage(legacy);
        }
        //Formats chat
        if (Config.CHAT_FORMATTER_ENABLED.getBoolean()) {
            String format = Config.CHAT_FORMAT.get();
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
            LangBuilder builder = new LangBuilder(format);
            //Translates colors for chat format
            format = builder.buildLegacyString();
            try {
                e.setFormat(format);
            } catch (Exception ex) {
                System.out.println("Error while formatting chat message! " + ex.getMessage());
            }
        }
    }
}



