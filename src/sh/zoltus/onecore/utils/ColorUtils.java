package sh.zoltus.onecore.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {

    // for detecting #8f8f8f
    private static final Pattern pattern = Pattern.compile("(?<!\\\\)(#[a-fA-F\\d]{6})");
    // for detecting &x&8&f&8&f&8&f in message
    private static final Pattern pattern2 = Pattern.compile("(?<!\\\\)(§x[a-fA-F\\d§]{12})");

    /**
     * TODO fix converting, check from old source This is messed up
     * Converts §x§8§f§8§f§8§f to #8f8f8f
     *
     * @param message Message containing symbolized hex (§x§8§f§8§f§8§f)
     * @return returns converted message
     */
    private static String decolorizeHex(String message) {
        Matcher matcher = pattern2.matcher(message);

        while (matcher.find()) {
            // Gets matched area from the find, in this case it gets §x§8§f§8§f§8§f
            String hexColorWSymbols = message.substring(matcher.start(), matcher.end());
            // and converts the result to #8f8f8f
            message = message.replace(hexColorWSymbols, hexColorWSymbols.replaceAll("§", "").replaceAll("(?i)x", "#"));
        }
        return message;
    }

    /**
     * Converts §x§8§f§8§f§8§f to #8f8f8f
     *
     * @param message Message containing hex (#8f8f8f)
     * @return returns converted message
     */
    public static String colorizeHex(String message) {
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, String.valueOf(ChatColor.of(color)));
        }
        return message;
    }

    /**
     * Colorizes § symbols
     *
     * @param message Colorizes message
     * @return Colorized String
     */
    public static String colorize(String message) {
        return message.replaceAll("&", "§");
    }

    /**
     * Colorizes § symbols And Hex
     *
     * @param message Colorizes message
     * @return Colorized String
     */
    public static String colorizeAll(String message) {
        //todo fix hex
        // return colorize(colorizeHex(message));
        return colorize(message);
    }

    /**
     * deColorizes § symbols And Hex
     *
     * @param message deColorizes message
     * @return deColorized String
     */
    public static String deColorizeAll(String message) {
        //todo fix hex
        // message = decolorizeHex(message);
        return message.replaceAll("§", "&");
    }


}
