package sh.zoltus.onecore.player.command.commands.teleport;

import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.User;
import sh.zoltus.onecore.player.command.arguments.RequestArgument;
import sh.zoltus.onecore.player.command.commands.teleport.handlers.Request;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.TP_NO_REQUESTS;

public class Tpaccept implements IOneCommand {

    @Override
    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                //tpaccept
                command(TPACCEPT_LABEL)
                        .withPermission(TPACCEPT_PERMISSION)
                        .withAliases(TPACCEPT_ALIASES)
                        .executesUser((sender, args) -> handle(Request.getLatest(sender), sender)),

                //tpaccept <player>
                command(TPACCEPT_LABEL)
                        .withPermission(TPACCEPT_PERMISSION)
                        .withAliases(TPACCEPT_ALIASES)
                        .withArguments(new RequestArgument())
                        .executesUser((sender, args) -> handle(Request.get((User) args[0], sender), sender)),
        };
    }

    private void handle(Request request, User sender) {
       if (request == null) {
            sender.sendMessage(TP_NO_REQUESTS.getString());
        } else {
            request.accept();
        }
    }
}
