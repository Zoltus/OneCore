package io.github.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.IArgument;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.concurrent.CompletableFuture;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.NODES_HOME_NAME;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.PLAYER_NEVER_VISITED_SERVER;

public class HomeArg1 extends CustomArgument<String, String> implements IArgument {
    //home <player> <home> <--
    //Delhome <player> <home> <--
    //Returns String
    public HomeArg1() {
        super(new StringArgument(NODES_HOME_NAME.getString()), (info) -> {
            String input = info.input();
            String prevArg = (String) info.previousArgs()[0];
            OfflinePlayer offP = Bukkit.getOfflinePlayer(prevArg);
            if (!offP.hasPlayedBefore()) {
                throw new CustomArgument.CustomArgumentException(PLAYER_NEVER_VISITED_SERVER.getString());
            } else {
                return input;
            }
        });

        replaceSuggestions(ArgumentSuggestions.stringsAsync(info -> CompletableFuture.supplyAsync(() -> {
            String prevArg = (String) info.previousArgs()[0];

            //If arg lenght 0 it will try to asyncload first argument as oneuser from db
            OfflinePlayer offP = Bukkit.getOfflinePlayer(prevArg);
            User target = User.of(offP);

            if (target != null) {
                return filter(info.currentArg(), target.getHomeArray());
            } else {
                return filter(info.currentArg());
            }
        })));
    }

    /*
        //Stays in memory, Could be abused by players with perm, Stays in memory to prevent spam scans
        private static final List<String> tasks = new ArrayList<>();

        /**
         * Tries to load target async for the next argument /home <player> <home> <--
         * tasks list is to prevent trying to load same target multiple times since load is async
         *
         * @param target offlinePlayer
         */
   /* private static void tryLoad(String target) {
        if (!tasks.contains(target) && OneUser.ofNullable(Bukkit.getOfflinePlayer(target)) == null /* && target.hasPlayedBefore()) {*/
         /*   tasks.add(target);
            Database.database().loadPlayer(Bukkit.getOfflinePlayer(target));
        }
    }
    */
}
