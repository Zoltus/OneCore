package io.github.zoltus.onecore.player.command.commands.admin;

import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;

public class ClearChat implements ICommand {

    @Override
    public void init() {
        new Command(Commands.CLEARCHAT_LABEL)
                .withPermission(Commands.CLEARCHAT_PERMISSION)
                .withAliases(Commands.CLEARCHAT_ALIASES)
                .executes((sender, args) -> {
                    Bukkit.broadcastMessage(StringUtils.repeat(" \n", 100));
                    Bukkit.broadcastMessage(Lang.CLEAR_PLAYER_CLEARED_CHAT.getString());
                }).override();
    }
}
