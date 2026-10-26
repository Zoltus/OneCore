package io.github.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import io.github.zoltus.onecore.player.command.IArgument;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.NODES_PLAYER;

public class PlayerArgument extends EntitySelectorArgument.OnePlayer implements IArgument {

    //Used so I wouldnt have to repeat node name.
    public PlayerArgument() {
        this("");
    }

    public PlayerArgument(String add) {
        super(NODES_PLAYER.get() + add);
        replaceSuggestions(ArgumentSuggestions
                .strings(info -> playerSuggestions(info.currentArg())));
    }
}
