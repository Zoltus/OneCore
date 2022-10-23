package io.github.zoltus.onecore.player.command.arguments;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.NODES_PLAYER;

public class PlayerArgument extends dev.jorel.commandapi.arguments.PlayerArgument {

    //Used so I wouldnt have to repeat node name.
    public PlayerArgument() {
        this("");
    }
    //todo remove @everyone from this arg switch to entityselector
    public PlayerArgument(String add) {
        super(NODES_PLAYER.getString() + add);
    }
}
