package io.github.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PlayerArgument;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.IArgument;
import org.bukkit.entity.Player;

public class RequestArgument extends PlayerArgument implements IArgument {
    public RequestArgument() {
        this("");
    }

    public RequestArgument(String add) {
        super(add);
        replaceSuggestions(ArgumentSuggestions
                .strings(info -> filter(info.currentArg(), User.of((Player) info.sender())
                .getRequests()
                .stream().map(request -> request.getSender().getName())
                .toArray(String[]::new))));
    }
}
