package sh.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import sh.zoltus.onecore.player.command.OneArgument;

import static sh.zoltus.onecore.configuration.yamls.Lang.NODES_PLAYER;
import static sh.zoltus.onecore.configuration.yamls.Lang.PLAYER_NEVER_VISITED_SERVER;

public class OfflinePlayerArgument extends OneArgument {

    public OfflinePlayerArgument() {
        this("");
    }

    public OfflinePlayerArgument(String add) {
        super(NODES_PLAYER.getString() + add, (info) -> {
            String input = info.input();
            OfflinePlayer offp = Bukkit.getOfflinePlayer(input);
            if (!offp.hasPlayedBefore()) {
                throw new CustomArgumentException(PLAYER_NEVER_VISITED_SERVER.getString());
            } else {
                return offp;
            }
        });
        //todo test
        replaceSuggestions(new PlayerArgument().getIncludedSuggestions().orElse(ArgumentSuggestions.empty()));
    }
}
