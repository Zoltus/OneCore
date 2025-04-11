package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.RequestArgument;
import io.github.zoltus.onecore.player.teleporting.Request;
import io.github.zoltus.onecore.player.User;
import org.bukkit.entity.Player;


public class TpDeny implements ICommand {

    //todo request argument
    @Override
    public void init() {
        //tpdeny <player>
        Argument<?> arg0 = new RequestArgument()
                .executesPlayer((player, args) -> {
                    User sender = User.of(player);
                    User target = User.of((Player) args.get(0));
                    Request request = Request.get(target, sender);
                    handle(request, sender);
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
            Lang.TP_NO_REQUESTS.send(sender);
        } else {
            request.deny();
        }
    }
}
