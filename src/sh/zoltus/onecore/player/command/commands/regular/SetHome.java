package sh.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.arguments.StringArgument;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.User;
import sh.zoltus.onecore.player.command.arguments.UserArgument;
import sh.zoltus.onecore.player.home.HomeAction;
import sh.zoltus.onecore.player.home.HomeHandler;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.*;

public class SetHome implements IOneCommand {

    //todo homelimit test
    @Override
    public void init() {
        //sethome
        command(SETHOME_LABEL)
                .withPermission(SETHOME_PERMISSION)
                .withAliases(SETHOME_ALIASES)
                .executesPlayer((p, args) -> {
                    HomeHandler.handle(p, p, null, HomeAction.SET);
                }).override();
        //sethome <home>
        command(SETHOME_LABEL)
                .withPermission(SETHOME_PERMISSION)
                .withAliases(SETHOME_ALIASES)
                .withArguments(new StringArgument(NODES_HOME_NAME.getString()))
                .executesPlayer((p, args) -> {
                    HomeHandler.handle(p, p, (String) args[0], HomeAction.SET);
                }).register();
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
                }).register();
    }
}
