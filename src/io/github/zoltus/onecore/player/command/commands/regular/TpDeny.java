package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.ArgumentTree;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.IOneCommand;
import io.github.zoltus.onecore.player.command.arguments.RequestArgument;
import io.github.zoltus.onecore.player.teleporting.Request;
import io.github.zoltus.onecore.player.User;


public class TpDeny implements IOneCommand {

    //todo request argument
    @Override
    public void init() {
        //tpdeny <player>
        ArgumentTree arg0 = new RequestArgument()
                .executesPlayer((player, args) -> {
                    User user = User.of(player);
                    handle(Request.get((User) args[0], user), user);
                });
        //tpdeny
        new Command(Commands.TPDENY_LABEL)
                .withPermission(Commands.TPDENY_PERMISSION)
                .withAliases(Commands.TPDENY_ALIASES)
                .executesPlayer((player, args) -> {
                    User user = User.of(player);
                    handle(Request.getLatest(user), user);
                }).then(arg0)
                .override();
    }

    private void handle(Request request, User sender) {
        if (request == null) {
            sender.sendMessage(Lang.TP_NO_REQUESTS.getString());
        } else {
            request.deny();
        }
    }
}
