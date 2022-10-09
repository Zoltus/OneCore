package sh.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.User;
import sh.zoltus.onecore.player.command.ApiCommand;

public class RequestArgument extends PlayerArgument {
    public RequestArgument() {
        this("");
    }

    public RequestArgument(String add) {
        super(add);
        replaceSuggestions(ArgumentSuggestions
                .strings(info -> ApiCommand.filter(info.currentArg(), User.of((Player) info.sender())
                .getRequests()
                .stream().map(request -> request.getSender().getName())
                .toArray(String[]::new))));
    }
}
