package io.github.zoltus.onecore.data.configuration.test;

import io.github.zoltus.onecore.data.configuration.OneYml;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Test implements ITest {
    TESTVAR("testvar");

    @Getter
    final String path;

    public static OneYml getYml() {
        return ITest.getYml1(Test.class);
    }
}
