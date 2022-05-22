package sh.zoltus.onecore.player.command.commands.home;

import dev.jorel.commandapi.arguments.StringArgument;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.User;
import sh.zoltus.onecore.player.command.arguments.UserArgument;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.NODES_HOME_NAME;

public class SetHome implements IOneCommand {

    //todo homelimit test
    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                //sethome
                command(SETHOME_LABEL)
                        .withPermission(SETHOME_PERMISSION)
                        .withAliases(SETHOME_ALIASES)
                        .executesPlayer((p, args) -> {
                    HomeHandler.handle(p, p, null, HomeAction.SET);
                }),
                //sethome <home>
                command(SETHOME_LABEL)
                        .withPermission(SETHOME_PERMISSION)
                        .withAliases(SETHOME_ALIASES)
                        .withArguments(new StringArgument(NODES_HOME_NAME.getString()))
                        .executesPlayer((p, args) -> {
                    HomeHandler.handle(p, p, (String) args[0], HomeAction.SET);
                }),
                //sethome <home> <player> //todo messages
                command(SETHOME_LABEL)
                        .withPermission(SETHOME_PERMISSION_OTHER)
                        .withAliases(SETHOME_ALIASES)
                        .withArguments(new StringArgument(NODES_HOME_NAME.getString()))
                        .withArguments(new UserArgument())
                        .executes((p, args) -> {
                    //todo setothermsg
                    User target = (User) args[0];
                    HomeHandler.handle(p, target.getPlayer(), (String) args[0], HomeAction.SET);
                })
        };
    }
}
