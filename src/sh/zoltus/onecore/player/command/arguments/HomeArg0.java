package sh.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.User;
import sh.zoltus.onecore.player.command.OneArgument;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.HOME_PERMISSION_OTHER;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.NODES_HOME_NAME_OR_Player;

public class HomeArg0 extends StringArgument implements OneArgument {

    //try to register cmd as /home <player> and /home <string>
    //todo DONE, Test
    public HomeArg0() {
        super(NODES_HOME_NAME_OR_Player.getString());
        replaceSuggestions(ArgumentSuggestions.strings(info -> {
            CommandSender sender = info.sender();
            User target = User.of((Player) sender);
            String[] homes = target.getHomeArray();
            if (!sender.hasPermission(HOME_PERMISSION_OTHER.asPermission())) {
                return homes;
            } else {
                return (String[]) ArrayUtils.addAll(homes, playerSuggestions(info.currentArg()));
            }
        }));
    }
}
