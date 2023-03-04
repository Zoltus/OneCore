package io.github.zoltus.onecore.utils;

import io.github.zoltus.onecore.OneCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {

    private static OneCore plugin = OneCore.getPlugin();
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer lcs = LegacyComponentSerializer.legacySection();
    // for detecting #8f8f8f
    private static final Pattern pattern = Pattern.compile("#\\p{XDigit}{6}");
    // for detecting §x§8§f§8§f§8§f
    private static final Pattern pattern2 = Pattern
            .compile("&x&\\p{XDigit}&\\p{XDigit}&\\p{XDigit}&\\p{XDigit}&\\p{XDigit}&\\p{XDigit}");



    /**
     * Converts §x§8§f§8§f§8§f to #8f8f8f
     *
     * @param message Message containing symbolized hex (§x§8§f§8§f§8§f)
     * @return returns converted message
     */
    public static String toNormal(String message) {
        message = message.replaceAll("§", "&");
        Matcher matcher = pattern2.matcher(message);
        StringBuilder sb = new StringBuilder(message);
        while (matcher.find()) {
            String hex = matcher.group()
                    .replace("&", "")
                    .replaceFirst("x", "#");
            sb.replace(matcher.start(), matcher.end(), hex);
        }
        return sb.toString();
    }

    /**
     * Converts #8f8f8f to §x§8§f§8§f§8§f
     *
     * @param message Message containing hex (#8f8f8f)
     * @return returns converted message
     */
    public static String toMineHex(String message) {
        Matcher matcher = pattern.matcher(message);
        StringBuilder sb = new StringBuilder(message);
        while (matcher.find()) {
            String color = matcher.group();
            sb.replace(matcher.start(), matcher.end(), String.valueOf(ChatColor.of(color)));
        }
        return ChatColor.translateAlternateColorCodes('&', sb.toString());
    }

    //Todo check if hex works
    public static Component translateColors(String str) {
        //Converts &6test &1message to <green>test <blue>message
        TextComponent deserialize = lcs.deserialize(str);
        String format = mm.serialize(deserialize);
        //Removes escapes \
        String replace = format.replace("\\<", "<");
        return mm.deserialize(replace);
    }

    public static void mmSend(CommandSender sender, String str) {
        plugin.adventure().sender(sender).sendMessage(translateColors(str));
    }
}
