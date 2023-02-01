package io.github.zoltus.onecore.data.configuration.test;

import io.github.zoltus.onecore.data.configuration.OneYml;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Test2 implements ITest {
    ;

    @Getter
    final String path;

    public static OneYml getYml() {
        return ITest.getYml1(Test2.class);
    }
}
