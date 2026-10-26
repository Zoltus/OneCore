package io.github.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.IArgument;
import io.github.zoltus.onecore.data.configuration.LangBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.NODES_HOME_NAME;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.PLAYER_NEVER_VISITED_SERVER;

public class HomeArg1 extends CustomArgument<String, String> implements IArgument {
    //home <player> <home> <--
    //Delhome <player> <home> <--
    //Returns String
    public HomeArg1() {
        super(new StringArgument(NODES_HOME_NAME.get()), info -> {
            String input = info.input();
            String prevArg = (String) info.previousArgs().get(0);
            //Player, autocompletes search.
            Player p = Bukkit.getPlayer(prevArg);
            OfflinePlayer offP = Bukkit.getOfflinePlayer(prevArg);
            if (p != null) {
                return input;
            } else if (!offP.hasPlayedBefore() || User.of(offP) == null) {
                throw CustomArgumentException.fromAdventureComponent(new LangBuilder(PLAYER_NEVER_VISITED_SERVER).buildComponent());
            } else {
                return input;
            }
        });
        replaceSuggestions(ArgumentSuggestions.strings(info -> {
            String prevArg = (String) info.previousArgs().get(0);
            OfflinePlayer offP = Bukkit.getOfflinePlayer(prevArg);
            User target = User.of(offP);
            return target != null ? filter(info.currentArg(), target.getHomeArray()) : filter(info.currentArg());
        }));
    }
}
