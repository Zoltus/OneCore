package sh.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.OneArgument;
import sh.zoltus.onecore.player.command.User;

import static sh.zoltus.onecore.configuration.yamls.Commands.HOME_PERMISSION_OTHER;
import static sh.zoltus.onecore.configuration.yamls.Lang.NODES_HOME_NAME_OR_Player;

public class HomeArg0 extends OneArgument {
    //try to register cmd as /home <player> and /home <string>

    public HomeArg0() { //todo to stringarg with replace
        super(NODES_HOME_NAME_OR_Player.getString(), CustomArgumentInfo::input);
        replaceSuggestions(info -> {
            CommandSender sender = info.sender();
            User target = User.of((Player) sender);
            String[] homes = target.getHomeArray();
            if (!sender.hasPermission(HOME_PERMISSION_OTHER.getAsPermission())) {
                return homes;
            } else {
                return (String[]) ArrayUtils.addAll(homes, playerSuggestions(info.currentInput()));
            }
        });
    }
}
