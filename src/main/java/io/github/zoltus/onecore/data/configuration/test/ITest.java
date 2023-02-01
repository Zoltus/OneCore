package io.github.zoltus.onecore.data.configuration.test;

import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.OneYml;

import java.util.HashMap;

public interface ITest {
    // OneYml yml();
    String getPath();

    HashMap<Class<? extends ITest>, OneYml> testMap = new HashMap<>();

    static OneYml getYml1(Class<? extends ITest> clazz) {
        if (!testMap.containsKey(clazz)) {
            String name = clazz.getSimpleName().toLowerCase() + ".yml";
            testMap.put(clazz, new OneYml(name, OneCore.getPlugin().getDataFolder()));
        }
        return testMap.get(clazz);
    }

    default <T> T get() {
        return (T) getYml1(this.getClass()).get(getPath());
    }
}