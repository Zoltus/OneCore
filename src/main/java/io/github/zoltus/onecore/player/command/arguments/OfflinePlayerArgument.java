package io.github.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.IArgument;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.NODES_PLAYER;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.PLAYER_NEVER_VISITED_SERVER;

public class OfflinePlayerArgument extends CustomArgument<OfflinePlayer, String> implements IArgument {

    public OfflinePlayerArgument() {
        this("");
    }

    public OfflinePlayerArgument(String add) {
        super(new StringArgument(NODES_PLAYER.getString() + add), info -> {
            String input = info.input();
            Player p = Bukkit.getPlayer(input);
            //priority to online players, this adds support for auto finding player
            //For example /tp zo, will find zoltus if online.
            if (p != null) {
                return p;
            } else {
                OfflinePlayer offP = Bukkit.getOfflinePlayer(input);
                //Bukkit.getOfflinePlayers()
                if (!offP.hasPlayedBefore() || User.of(offP) == null) {
                    throw new CustomArgumentException(PLAYER_NEVER_VISITED_SERVER.getString());
                } else {
                    return offP;
                }
            }
        });
        replaceSuggestions(ArgumentSuggestions
                .strings(info -> playerSuggestions(info.currentArg())));
    }
}
