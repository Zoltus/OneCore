package sh.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.OneArgument;
import sh.zoltus.onecore.player.User;

import static sh.zoltus.onecore.data.configuration.yamls.Lang.*;

//Only for online players
public class UserArgument extends CustomArgument<User, String> implements OneArgument  {

    //todo remove
    public UserArgument() {
        this("");
    }

    public UserArgument(String add) {
        super(new StringArgument(NODES_PLAYER.getString() + add), (info) -> {
            String input = info.input();
            Player player = Bukkit.getPlayer(input);
            if (player == null) {
                throw new CustomArgument.CustomArgumentException(PLAYER_NOT_FOUND.getString());
            } else {
                return User.get(player);
            }
        });
        replaceSuggestions(ArgumentSuggestions
                .strings((info) -> playerSuggestions(info.currentArg())));
    }
}
