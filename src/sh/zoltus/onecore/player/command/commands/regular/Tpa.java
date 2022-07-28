package sh.zoltus.onecore.player.command.commands.regular;

import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.User;
import sh.zoltus.onecore.player.command.arguments.UserArgument;
import sh.zoltus.onecore.player.teleporting.Request;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;


public class Tpa implements IOneCommand {

    @Override
    public void init() {
        //tpa <player>
        command(TPA_LABEL)
                .withPermission(TPA_PERMISSION)
                .withAliases(TPA_ALIASES)
                .withArguments(new UserArgument())
                .executesUser((sender, args) -> Request.send(sender, (User) args[0], Request.TeleportType.TPA))
                .override();
    }
}
