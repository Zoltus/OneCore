package sh.zoltus.onecore.player.command.commands.teleport;

import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.User;
import sh.zoltus.onecore.player.command.arguments.RequestArgument;
import sh.zoltus.onecore.player.command.commands.teleport.handlers.Request;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.TP_NO_REQUESTS;


public class TpDeny implements IOneCommand {

    //todo request argument
    @Override
    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                //tpdeny
                command(TPDENY_LABEL)
                        .withPermission(TPDENY_PERMISSION)
                        .withAliases(TPDENY_ALIASES)
                        .executesUser((sender, args) -> handle(Request.getLatest(sender), sender)),

                //tpdeny <player>
                command(TPDENY_LABEL)
                        .withPermission(TPDENY_PERMISSION)
                        .withAliases(TPDENY_ALIASES)
                        .withArguments(new RequestArgument())
                        .executesUser((sender, args) -> handle(Request.get((User) args[0], sender), sender)),
        };
    }

    private void handle(Request request, User sender) {
        if (request == null) {
            sender.sendMessage(TP_NO_REQUESTS.getString());
        } else {
            request.deny();
        }
    }
}
