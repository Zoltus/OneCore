package io.github.zoltus.onecore.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {


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

    // For Join and quit
/*    public static String asLegacy(String str) {
        Component deserialized = toMiniMessage(str);
        return mm.serialize(deserialized);
    }*/
/*
    public static Component translateColors(String str) {
        //Converts &6test &1message to <green>test <blue>message
        TextComponent deserialize = lcs.deserialize(str);
        String format = mm.serialize(deserialize);
        //Removes escapes \
        String replace = format.replace("\\<", "<");
        //<hover:show_text:<red>Paina hylkääksesi!><click:run_command:/tpdeny>Hylkää!</click></hover>
        return mm.deserialize(replace);
    }*/

/*    public static void mmSend(CommandSender sender, String str) {
        BukkitAudiences adventure = plugin.adventure();
        Audience audience = adventure.sender(sender);
        audience.sendMessage(translateColors(str));
    }*/
}
