package io.github.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import io.github.zoltus.onecore.player.command.IArgument;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.NODES_PLAYER;

public class PlayerArgument extends dev.jorel.commandapi.arguments.PlayerArgument implements IArgument {

    //Used so I wouldnt have to repeat node name.
    public PlayerArgument() {
        this("");
    }

    public PlayerArgument(String add) {
        super(NODES_PLAYER.getString() + add);
        replaceSuggestions(ArgumentSuggestions
                .strings(info -> playerSuggestions(info.currentArg())));
    }
}
