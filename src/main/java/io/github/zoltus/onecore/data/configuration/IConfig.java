package io.github.zoltus.onecore.data.configuration;

import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.github.zoltus.onecore.listeners.ChatListener;

import java.util.List;
import java.util.regex.Pattern;

public interface IConfig {
    //Placeholders
    String PLAYER_PH = "<player>";
    String PLAYER2_PH = "<player2>";
    String AMOUNT_PH = "<amount>";
    String BALANCE_PH = "<balance>";
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
    String LINE_PH = "<line>";
    String PERM_PH = "<perm>";

    OneYml yml();
    String getPath();

    default String getString() {
        String prefix = Config.PREFIX.get();
        String configValue = yml().getOrDefault("Data." + getPath());
        String message = configValue.replaceAll(Pattern.quote("{p}"), prefix);
        return ChatListener.translateColors(message);
    }

    default String asPermission() {
        String configValue = yml().getOrDefault("Data." + getPath());
        return Config.PERMISSION_PREFIX.get() + configValue;
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

    default long getLong() {
        return yml().getLong("Data." + getPath());
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
}
