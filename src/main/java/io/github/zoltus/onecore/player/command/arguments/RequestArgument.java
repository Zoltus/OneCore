package io.github.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PlayerArgument;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.IArgument;
import io.github.zoltus.onecore.player.teleporting.Request;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RequestArgument extends PlayerArgument implements IArgument {
    public RequestArgument() {
        this("");
    }

    public RequestArgument(String add) {
        super(add);
        replaceSuggestions(ArgumentSuggestions
                .strings(info -> {
                    List<String> list = new ArrayList<>();
                    for (Request request : User.of((Player) info.sender())
                            .getRequests()) {
                        String name = request.getSender().getName();
                        list.add(name);
                    }
                    return filter(info.currentArg(), list.toArray(new String[0]));
                }));
    }
}
