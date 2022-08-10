package sh.zoltus.onecore.configuration;


import lombok.Getter;
import sh.zoltus.onecore.OneCore;

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
    Console_Filter("console_filter.yml"),
    LANG("lang.yml"),
    WARPS("warps.yml");

    @Getter
    private final OneYml yml;

    Yamls(String fileName) {
        this.yml = new OneYml(fileName, OneCore.getPlugin().getDataFolder());
    }
}