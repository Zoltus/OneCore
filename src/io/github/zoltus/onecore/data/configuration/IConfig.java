package io.github.zoltus.onecore.data.configuration;

import net.md_5.bungee.api.ChatColor;

import java.util.List;
import java.util.regex.Pattern;

import static io.github.zoltus.onecore.data.configuration.yamls.Config.PERMISSION_PREFIX;

public interface IConfig {

    //Placeholders
    String PLAYER_PH = "<player>";
    String PLAYER2_PH = "<player2>";
    String AMOUNT_PH = "<amount>";
    String BALANCE_PH = "<balance>";
    String SIZE_PH = "<size>";
    String HOME_PH = "<home>";
    String TOGGLE_PH = "<toggle>";
    String SECONDS_PH = "<seconds>";
    String MODE_PH = "<mode>";
    String LIST_PH = "<list>";
    String TYPE_PH = "<type>";
    String RADIUS_PH = "<radius>";
    String MESSAGE_PH = "<message>";
    String PING_PH = "<ping>";
    String TIME_PH = "<time>";
    String SLOT_PH = "<slot>";
    String WARP_PH = "<warp>";
    String USED_PH = "<used>";
    String TOTAL_PH = "<total>";
    String SYSTEM_PH = "<system-os>";
    String PROCESSORS_PH = "<processors>";
    String VERSION_PH = "<version>";
    String USERNAME_PH = "<username>";
    String WORLD_PH = "<world>";
    String WEATHER_PH = "<weather>";
    String ACCEPT_PH = "<accept>";//Todo
    String DENY_PH = "<deny>";//Todo

    OneYml yml();

    String getPath();

    default String getString() {
        String prefix = Yamls.CONFIG.getYml().getOrDefault("Data.prefix", "null");
        String configValue = yml().getOrDefault("Data." + getPath(), "null");
        String message = configValue.replaceAll(Pattern.quote("{p}"), prefix);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    default String asPermission() {
        String configValue = yml().getOrDefault("Data." + getPath(), "null");
        return PERMISSION_PREFIX.get() + configValue;
    }

    default boolean getBoolean() {
        return yml().getBoolean("Data." + getPath());
    }

    default <T> T get() {
        return (T) yml().get("Data." + getPath());
    }

    default int getInt() {
        return yml().getInt("Data." + getPath());
    }

    default double getDouble() {
        return yml().getDouble("Data." + getPath());
    }

    default String[] getAsArray() {
        return getList().toArray(String[]::new);
    }

    default List<String> getList() {
        return yml().getStringList("Data." + getPath());
    }

    default String rp(Object... replaces) {
        String ph = null;
        String message = getString();
        for (Object objRp : replaces) {
            String replace = objRp instanceof IConfig config ? config.getString() : String.valueOf(objRp);
            if (ph == null) {
                ph = replace;
            } else {
                message = message.replaceAll(ph, replace);
                ph = null;
            }
        }
        return message;
    }

}
