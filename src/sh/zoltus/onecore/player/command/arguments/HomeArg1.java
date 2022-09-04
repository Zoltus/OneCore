package sh.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import sh.zoltus.onecore.OneCore;
import sh.zoltus.onecore.data.database.Database;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.OneArgument;
import sh.zoltus.onecore.player.command.User;

import java.util.concurrent.CompletableFuture;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.*;

public class HomeArg1 extends CustomArgument<String, String> implements OneArgument  {
    //home <player> <home> <--
    //Delhome <player> <home> <--
    public HomeArg1() {
        super(new StringArgument(NODES_HOME_NAME.getString()), (info) -> {
            String input = info.input();
            String prevArg = (String) info.previousArgs()[0];
            User user = getPrevArgTarget(prevArg);

            if (user == null) {
                throw new CustomArgument.CustomArgumentException(PLAYER_NOT_FOUND.getString());
            } else if (!user.hasHome(input)) {
                throw new CustomArgument.CustomArgumentException(HOME_LIST.rp(LIST_PH, user.getHomes().keySet()));
            } else {
                return input;
            }
        });

        replaceSuggestions(ArgumentSuggestions.stringsAsync(info -> CompletableFuture.supplyAsync(() -> {
            String prevArg = (String) info.previousArgs()[0];
            //If arg lenght 0 it will try to asyncload first argument as oneuser from db
            if (info.currentArg().isEmpty()) {
                //tryLoad(prevArg);
                //Tries to load player
                //todo
                OneCore.getPlugin().getDatabase().loadPlayer(Bukkit.getOfflinePlayer(prevArg));
            }
            User target = getPrevArgTarget(prevArg);
            if (target != null) {
                return ApiCommand.filter(info.currentArg(), target.getHomeArray());
            } else {
                return ApiCommand.filter(info.currentArg());
            }
        })));
    }

    //HomeBothArg sends asyncload for this argument
    private static User getPrevArgTarget(String prevArg) {
        return User.ofNullable(Bukkit.getOfflinePlayer(prevArg));
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
