package sh.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import sh.zoltus.onecore.player.User;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.NODES_HOME_NAME;

public class SetHome implements IOneCommand {

    //todo homelimit test
    @Override
    public void init() {
        //sethome
        command(SETHOME_LABEL)
                .withPermission(SETHOME_PERMISSION)
                .withAliases(SETHOME_ALIASES)
                .executesPlayer((p, args) -> {
                    Home.handle(p, User.of(p), null, Home.Action.SET);
                }).override();
        //sethome <home>
        command(SETHOME_LABEL)
                .withPermission(SETHOME_PERMISSION)
                .withAliases(SETHOME_ALIASES)
                .withArguments(new StringArgument(NODES_HOME_NAME.getString()))
                .executesPlayer((p, args) -> {
                    Home.handle(p, User.of(p), (String) args[0], Home.Action.SET);
                }).register();
        //sethome <home> <player> //todo messages
        command(SETHOME_LABEL)
                .withPermission(SETHOME_PERMISSION_OTHER)
                .withAliases(SETHOME_ALIASES)
                .withArguments(new StringArgument(NODES_HOME_NAME.getString()))
                .withArguments(new OfflinePlayerArgument())
                .executes((sender, args) -> {
                    OfflinePlayer offP = Bukkit.getOfflinePlayer((String) args[0]);
                    Home.handle(sender, User.of(offP), (String) args[1], Home.Action.SET);
                }).register();
    }
}


/*


 */