package sh.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import sh.zoltus.onecore.player.User;
import sh.zoltus.onecore.player.command.OneArgument;

import static sh.zoltus.onecore.data.configuration.yamls.Lang.NODES_PLAYER;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.PLAYER_NEVER_VISITED_SERVER;

//Only for online players
public class UserArgument extends CustomArgument<User, String> implements OneArgument  {

    //todo remove
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
