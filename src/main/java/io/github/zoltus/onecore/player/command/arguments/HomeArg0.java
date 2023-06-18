package io.github.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.IArgument;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.HOME_PERMISSION_OTHER;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.NODES_HOME_NAME_OR_PLAYER;

public class HomeArg0 extends StringArgument implements IArgument {

    //try to register cmd as /home <player> and /home <string>
    public HomeArg0() {
        super(NODES_HOME_NAME_OR_PLAYER.getString());
        replaceSuggestions(ArgumentSuggestions.strings(info -> {
            CommandSender sender = info.sender();
            User target = User.of((Player) sender);
            String[] suggestions = target.getHomeArray();

            //Adds player suggestions if player is in creative or spectator mode and has perm
            Player player = (Player) sender;
            GameMode gm = player.getGameMode();
            if ((gm == GameMode.CREATIVE || gm == GameMode.SPECTATOR)
                    && player.hasPermission(HOME_PERMISSION_OTHER.asPermission())) {
                String[] targets = playerSuggestions(info.currentArg());
                suggestions = ArrayUtils.addAll(suggestions, targets);
            }
            return suggestions;
        }));
    }
}
