package sh.zoltus.onecore.player.command.commands.regular;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import sh.zoltus.onecore.player.User;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.HomeArg0;
import sh.zoltus.onecore.player.command.arguments.HomeArg1;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;

public class DelHome implements IOneCommand {
    @Override
    public void init() {
        //delhome <home>
        command(DELHOME_LABEL)
                .withPermission(DELHOME_PERMISSION)
                .withAliases(DELHOME_ALIASES)
                .withArguments(new HomeArg0())
                .executesPlayer((p, args) -> {
                    Home.handle(p, User.of(p), (String) args[0], Home.Action.DELETE);
                }).override();
        //delhome <player> <home>
        command(DELHOME_LABEL)
                .withPermission(DELHOME_PERMISSION)
                .withAliases(DELHOME_ALIASES)
                .withArguments(new HomeArg0(), new HomeArg1())
                .executes((sender, args) -> {
                    OfflinePlayer offP = Bukkit.getOfflinePlayer((String) args[0]);
                    Home.handle(sender, User.of(offP), (String) args[1], Home.Action.DELETE);
                }).register();
    }
}




















