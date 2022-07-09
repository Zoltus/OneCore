package sh.zoltus.onecore.player.command.commands.regular;

import org.bukkit.Bukkit;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.HomeArg0;
import sh.zoltus.onecore.player.command.arguments.HomeArg1;
import sh.zoltus.onecore.player.home.HomeAction;
import sh.zoltus.onecore.player.home.HomeHandler;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;

public class DelHome implements IOneCommand {
    @Override
    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                //delhome <home>
                command(DELHOME_LABEL)
                        .withPermission(DELHOME_PERMISSION)
                        .withAliases(DELHOME_ALIASES)
                        .withArguments(new HomeArg0())
                        .executesPlayer((p, args) -> {
                    HomeHandler.handle(p, p, (String) args[0], HomeAction.DELETE);
                }),

                //delhome <player> <home>
                command(DELHOME_LABEL)
                        .withPermission(DELHOME_PERMISSION)
                        .withAliases(DELHOME_ALIASES)
                        .withArguments(new HomeArg0(), new HomeArg1())
                        .executes((sender, args) -> {
                    HomeHandler.handle(sender, Bukkit.getOfflinePlayer((String) args[0]), (String) args[0], HomeAction.DELETE);
                }),
        };
    }
}




















