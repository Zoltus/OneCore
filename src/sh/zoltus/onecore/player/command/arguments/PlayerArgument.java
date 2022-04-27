package sh.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.EntitySelectorArgument;

import static sh.zoltus.onecore.configuration.yamls.Lang.NODES_PLAYER;

public class PlayerArgument extends EntitySelectorArgument {

    public PlayerArgument() {
        this("");
    }

    public PlayerArgument(String add) {
        super(NODES_PLAYER.getString() + add, EntitySelector.ONE_PLAYER);
    }
}
