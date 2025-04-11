package io.github.zoltus.onecore.data.configuration;

import io.github.zoltus.onecore.data.configuration.yamls.Config;

import java.util.List;

public interface IConfig {
    OneYml yml();

    String getPath();

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

    default String asLegacyString() {
        LangBuilder builder = new LangBuilder(this);
        return builder.buildLegacyString();
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
