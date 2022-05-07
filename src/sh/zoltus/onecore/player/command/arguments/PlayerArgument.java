package sh.zoltus.onecore.player.command.arguments;

import static sh.zoltus.onecore.configuration.yamls.Lang.NODES_PLAYER;

public class PlayerArgument extends dev.jorel.commandapi.arguments.PlayerArgument {

    //Used so I wouldnt have to repeat node name.
    public PlayerArgument() {
        this("");
    }

    public PlayerArgument(String add) {
        super(NODES_PLAYER.getString() + add);
    }
}
