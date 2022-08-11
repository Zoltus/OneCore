package sh.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.OneArgument;
import sh.zoltus.onecore.player.command.User;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.HOME_PERMISSION_OTHER;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.NODES_HOME_NAME_OR_Player;

public class HomeArg0 extends CustomArgument<String, String> implements OneArgument {

    //try to register cmd as /home <player> and /home <string>
    public HomeArg0() {
        super(new StringArgument(NODES_HOME_NAME_OR_Player.getString()), CustomArgumentInfo::input);
       replaceSuggestions(ArgumentSuggestions.strings(info -> {
           CommandSender sender = info.sender();
           User target = User.of((Player) sender);
           String[] homes = target.getHomeArray();
           if (!sender.hasPermission(HOME_PERMISSION_OTHER.getAsPermission())) {
               return homes;
           } else {
               return (String[]) ArrayUtils.addAll(homes, playerSuggestions(info.currentInput()));
           }
       }));
    }
}
