package sh.zoltus.onecore.player.command.commands.regular;

import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.User;
import sh.zoltus.onecore.player.command.arguments.RequestArgument;
import sh.zoltus.onecore.player.teleporting.Request;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.*;


public class TpDeny implements IOneCommand {

    //todo request argument
    @Override
    public void init() {
        //tpdeny
        command(TPDENY_LABEL)
                .withPermission(TPDENY_PERMISSION)
                .withAliases(TPDENY_ALIASES)
                .executesUser((sender, args) -> handle(Request.getLatest(sender), sender))
                .override();
        //tpdeny <player>
        command(TPDENY_LABEL)
                .withPermission(TPDENY_PERMISSION)
                .withAliases(TPDENY_ALIASES)
                .withArguments(new RequestArgument())
                .executesUser((sender, args) -> handle(Request.get((User) args[0], sender), sender))
                .register();
    }

    private void handle(Request request, User sender) {
        if (request == null) {
            sender.sendMessage(TP_NO_REQUESTS.getString());
        } else {
            request.deny();
        }
    }
}
