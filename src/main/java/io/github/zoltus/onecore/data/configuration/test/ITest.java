package io.github.zoltus.onecore.data.configuration.test;

import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.OneYml;

import java.util.HashMap;

public interface ITest {
    String getPath();

    HashMap<Class<? extends ITest>, OneYml> testMap = new HashMap<>();

    static OneYml getYml1(Class<? extends ITest> clazz) {
        return testMap.computeIfAbsent(clazz, key -> {
            String name = key.getSimpleName().toLowerCase() + ".yml";
            return new OneYml(name, OneCore.getPlugin().getDataFolder());
        });
    }

    default <T> T get() {
        return (T) getYml1(this.getClass()).get(getPath());
    }
}