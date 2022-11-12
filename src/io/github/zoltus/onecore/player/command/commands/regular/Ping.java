package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.ArgumentTree;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.PlayerArgument;
import org.bukkit.entity.Player;
import io.github.zoltus.onecore.player.command.Command;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.PING_TARGETS_PING;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.PING_YOUR_PING;

public class Ping implements ICommand {

    @Override
    public void init() {
        //Ping <player>
        ArgumentTree arg0 = new PlayerArgument()
                .executes((sender, args) -> {
                    Player target = (Player) args[0];
                    PING_TARGETS_PING.send(target, PING_PH, target.getPing(), PLAYER_PH, target.getName());
                });
        //Ping
        new Command(PING_LABEL)
                .withPermission(PING_PERMISSION)
                .withAliases(PING_ALIASES)
                .executesPlayer((player, args) -> {
                    PING_YOUR_PING.send(player, PING_PH, player.getPing());
                }).then(arg0)
                .override();
    }
}
