package sh.zoltus.onecore.player.command.commands;

import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.PlayerArgument;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.PING_TARGETS_PING;
import static sh.zoltus.onecore.configuration.yamls.Lang.PING_YOUR_PING;

public class Ping implements IOneCommand {

    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                //Ping
                command(PING_LABEL)
                        .withPermission(PING_PERMISSION)
                        .withAliases(PING_ALIASES)
                        .executesPlayer((player, args) -> {
                    player.sendMessage(PING_YOUR_PING.rp(PING_PH, player.getPing()));
                }),
                //Ping <player>
                command(PING_LABEL)
                        .withPermission(PING_OTHER_PERMISSION)
                        .withAliases(PING_ALIASES)
                        .withArguments(new PlayerArgument())
                        .executes((sender, args) -> {
                    Player target = (Player) args[0];
                    sender.sendMessage(PING_TARGETS_PING.rp(PING_PH, target.getPing(), PLAYER_PH, target.getName()));
                })
        };
    }
}
