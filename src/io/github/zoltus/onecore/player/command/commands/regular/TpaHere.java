package io.github.zoltus.onecore.player.command.commands.regular;

import io.github.zoltus.onecore.player.command.IOneCommand;
import io.github.zoltus.onecore.player.command.arguments.UserArgument;
import org.bukkit.entity.Player;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.teleporting.Request;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;

public class TpaHere implements IOneCommand {
    @Override
    public void init() {
        //tpa <player>
        new Command(TPAHERE_LABEL)
                .withPermission(TPAHERE_PERMISSION)
                .withAliases(TPAHERE_ALIASES)
                .then(new UserArgument()
                .executesPlayer((player, args) -> {
                    User user = User.of(player);
                    User target = User.of((Player) args[0]);
                    Request.send(user, target, Request.TeleportType.TPHERE);
                })).override();
    }
}
