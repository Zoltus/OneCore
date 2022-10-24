package io.github.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.OneArgument;
import org.bukkit.Bukkit;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.NODES_PLAYER;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.PLAYER_NEVER_VISITED_SERVER;

public class UserArgument extends CustomArgument<User, String> implements OneArgument {

    public UserArgument() {
        this("");
    }

    public UserArgument(String add) {
        super(new StringArgument(NODES_PLAYER.getString() + add), (info) -> {
            String input = info.input();
            User user = User.of(Bukkit.getOfflinePlayer(input));
            if (user == null) {
                throw new CustomArgument.CustomArgumentException(PLAYER_NEVER_VISITED_SERVER.getString());
            } else {
                return user;
            }
        });
        replaceSuggestions(ArgumentSuggestions
                .strings((info) -> playerSuggestions(info.currentArg())));
    }
}
