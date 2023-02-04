package io.github.zoltus.onecore.player.command;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.executors.CommandExecutor;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import io.github.zoltus.onecore.data.configuration.IConfig;

public class Command extends CommandTree {

    //Used for hardcoded onecore command
    public Command(String commandName) {
        super(commandName);
    }

    public Command(IConfig enumz) {
        super(enumz.getString());
    }

    public Command withAliases(IConfig enumz) {
        super.withAliases(enumz.getAsArray());
        return this;
    }

    public Command withPermission(IConfig enumz) {
        super.withPermission(enumz.asPermission());
        return this;
    }

    @Override
    public Command executesPlayer(PlayerCommandExecutor executor) {
        super.executesPlayer(executor);
        return this;
    }

    public Command executes(CommandExecutor executor) {
        super.executes(executor);
        return this;
    }
}
