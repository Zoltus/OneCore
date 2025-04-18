package io.github.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.IArgument;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.NODES_PLAYER;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.PLAYER_NEVER_VISITED_SERVER;

public class UserArgument extends CustomArgument<User, String> implements IArgument {

    public UserArgument() {
        this("");
    }

    public UserArgument(String add) {
        super(new StringArgument(NODES_PLAYER.get() + add), info -> {
            String input = info.input();
            Player p = Bukkit.getPlayer(input);
            if (p != null) {
                return User.of(p);
            } else {
                OfflinePlayer offP = Bukkit.getOfflinePlayer(input);
                User user = User.of(offP);
                if (!offP.hasPlayedBefore() || user == null) {
                    throw CustomArgumentException
                            .fromBaseComponents(TextComponent.fromLegacyText(PLAYER_NEVER_VISITED_SERVER.get()));
                } else {
                    return user;
                }
            }
        });
        replaceSuggestions(ArgumentSuggestions
                .strings(info -> playerSuggestions(info.currentArg())));
    }
}
