package io.github.zoltus.onecore.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

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
                    .replaceAll("&", "")
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
    //For chatpreview
    public static BaseComponent[] toComponents(String text) {
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', text));
    }
}
