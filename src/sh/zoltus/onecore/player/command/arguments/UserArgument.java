package sh.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.OneArgument;
import sh.zoltus.onecore.player.command.User;

import static sh.zoltus.onecore.data.configuration.yamls.Lang.NODES_PLAYER;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.PLAYER_NEVER_VISITED_SERVER;

public class UserArgument extends CustomArgument<User, String> implements OneArgument  {

    public UserArgument() {
        this("");
    }

    public UserArgument(String add) {
        super(new StringArgument(NODES_PLAYER.getString() + add), (info) -> {
            String input = info.input();
            Player player = Bukkit.getPlayer(input);
            if (player == null) {
                throw new CustomArgument.CustomArgumentException(PLAYER_NEVER_VISITED_SERVER.getString());
            } else {
                return User.of(player);
            }
        });
        replaceSuggestions(ArgumentSuggestions
                .strings((info) -> playerSuggestions(info.currentArg())));
    }
}
