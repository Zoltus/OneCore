package sh.zoltus.onecore.player.command.commands.regular;

import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.User;
import sh.zoltus.onecore.player.command.arguments.UserArgument;
import sh.zoltus.onecore.player.teleporting.Request;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;


public class TpaHere implements IOneCommand {
    @Override
    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                //tpa <player>
                command(TPAHERE_LABEL)
                        .withPermission(TPAHERE_PERMISSION)
                        .withAliases(TPAHERE_ALIASES)
                        .withArguments(new UserArgument())
                        .executesUser((sender, args) -> Request.send(sender, (User) args[0], Request.TeleportType.TPHERE)),
        };
    }
}
