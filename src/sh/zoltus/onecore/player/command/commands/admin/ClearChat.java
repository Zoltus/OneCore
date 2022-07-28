package sh.zoltus.onecore.player.command.commands.admin;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import sh.zoltus.onecore.player.command.IOneCommand;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.CLEAR_PLAYER_CLEARED_CHAT;


public class ClearChat implements IOneCommand {

    @Override
    public void init() {
                command(CLEARCHAT_LABEL)
                        .withPermission(CLEARCHAT_PERMISSION)
                        .withAliases(CLEARCHAT_ALIASES)
                        .executes((sender, args) -> {
                    Bukkit.broadcastMessage(StringUtils.repeat(" \n", 100));
                    Bukkit.broadcastMessage(CLEAR_PLAYER_CLEARED_CHAT.getString());
                }).override();
    }
}
