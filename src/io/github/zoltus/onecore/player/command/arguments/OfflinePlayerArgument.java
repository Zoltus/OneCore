package io.github.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import io.github.zoltus.onecore.player.command.OneArgument;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.NODES_PLAYER;

public class OfflinePlayerArgument extends dev.jorel.commandapi.arguments.OfflinePlayerArgument implements OneArgument {

    public OfflinePlayerArgument() {
        this("");
    }

    public OfflinePlayerArgument(String add) {
        super(NODES_PLAYER.getString() + add);
        replaceSuggestions(ArgumentSuggestions
                .strings((info) -> playerSuggestions(info.currentArg())));
    }
}
