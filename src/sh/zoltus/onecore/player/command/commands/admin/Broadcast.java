package sh.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.Bukkit;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.BROADCAST_PREFIX;
import static sh.zoltus.onecore.configuration.yamls.Lang.NODES_MESSAGE;

public class Broadcast implements IOneCommand {
    @Override
    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                command(BROADCAST_LABEL)
                        .withPermission(BROADCAST_PERMISSION)
                        .withAliases(BROADCAST_ALIASES)
                        .withArguments(new GreedyStringArgument(NODES_MESSAGE.getString()))
                        .executes((sender, args) -> {
                    Bukkit.broadcastMessage(BROADCAST_PREFIX.getInt() + (String) args[0]);
                })
        };
    }
}
