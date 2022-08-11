package sh.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.OneArgument;
import sh.zoltus.onecore.player.command.User;

import static sh.zoltus.onecore.data.configuration.yamls.Lang.NODES_PLAYER;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.PLAYER_NEVER_VISITED_SERVER;

public class RequestArgument extends CustomArgument<User, String> implements OneArgument  {
    public RequestArgument() {
        this("");
    }

    public RequestArgument(String add) {
        super(new StringArgument(NODES_PLAYER.getString() + add), (info) -> {
            Player player = Bukkit.getPlayer(info.input());
            if (player == null) {
                throw new CustomArgument.CustomArgumentException(PLAYER_NEVER_VISITED_SERVER.getString());
            } else {
                return User.of(player);
            }
        });
        replaceSuggestions(ArgumentSuggestions
                .strings(info -> ApiCommand.filter(info.currentArg(), User.of((Player) info.sender())
                .getRequests()
                .stream().map(request -> request.getSender().getName())
                .toArray(String[]::new))));
    }
}
