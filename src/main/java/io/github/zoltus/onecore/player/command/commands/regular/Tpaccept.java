package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.RequestArgument;
import io.github.zoltus.onecore.player.teleporting.Request;
import org.bukkit.entity.Player;
import io.github.zoltus.onecore.player.User;

public class Tpaccept implements ICommand {
    @Override
    public void init() {
        //tpaccept <player>
        Argument<?> arg0 = new RequestArgument()
                .executesPlayer((player, args) -> {
                    handle(player, (Player) args.get(0));
                });
        //tpaccept
        new Command(Commands.TPACCEPT_LABEL)
                .withPermission(Commands.TPACCEPT_PERMISSION)
                .withAliases(Commands.TPACCEPT_ALIASES)
                .executesPlayer((player, args) -> {
                    handle(player, null);
                }).then(arg0)
                .override();
    }

    private void handle(Player accepter, Player target) {
        Request request;
        if (target == null) {
            request = Request.getLatest(User.of(accepter));
        } else {
            request = Request.get(User.of(target), User.of(accepter));
        }

        if (request == null) {
            accepter.sendMessage(Lang.TP_NO_REQUESTS.getString());
        } else {
            request.accept();
        }
    }
}
