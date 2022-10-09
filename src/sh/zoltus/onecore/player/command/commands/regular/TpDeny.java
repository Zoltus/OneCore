package sh.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.ArgumentTree;
import sh.zoltus.onecore.player.User;
import sh.zoltus.onecore.player.command.Command;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.RequestArgument;
import sh.zoltus.onecore.player.teleporting.Request;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.TP_NO_REQUESTS;


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
        new Command(TPDENY_LABEL)
                .withPermission(TPDENY_PERMISSION)
                .withAliases(TPDENY_ALIASES)
                .executesPlayer((player, args) -> {
                    User user = User.of(player);
                    handle(Request.getLatest(user), user);
                }).then(arg0)
                .override();
    }

    private void handle(Request request, User sender) {
        if (request == null) {
            sender.sendMessage(TP_NO_REQUESTS.getString());
        } else {
            request.deny();
        }
    }
}
