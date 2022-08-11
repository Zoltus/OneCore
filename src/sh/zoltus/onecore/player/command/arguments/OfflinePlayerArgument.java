package sh.zoltus.onecore.player.command.arguments;

import static sh.zoltus.onecore.data.configuration.yamls.Lang.NODES_PLAYER;

public class OfflinePlayerArgument extends dev.jorel.commandapi.arguments.OfflinePlayerArgument {

    public OfflinePlayerArgument() {
        this("");
    }

    public OfflinePlayerArgument(String add) {
        super(NODES_PLAYER.getString() + add);
    }
}
