package io.github.zoltus.onecore.data.configuration;


import io.github.zoltus.onecore.OneCore;
import lombok.Getter;

public enum Yamls {
 /*   BUKKIT(""),
    HELP(""),
    PAPER(""),
    PERMISSIONS(""),
    PUPUR(""),
    SPIGOT(""),
    TUINITY(""),*/

    COMMANDS("commands.yml"),
    CONFIG("config.yml"),
    CONSOLE_FILTER("console_filter.yml"),
    LANG("lang.yml"),
    WARPS("warps.yml");

    @Getter
    private final OneYml yml;

    Yamls(String fileName) {
        this.yml = new OneYml(fileName, OneCore.getPlugin().getDataFolder());
    }
}