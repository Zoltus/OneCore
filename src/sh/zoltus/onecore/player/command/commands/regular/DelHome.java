package sh.zoltus.onecore.player.command.commands.regular;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import sh.zoltus.onecore.player.User;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.HomeArg0;
import sh.zoltus.onecore.player.command.arguments.HomeArg1;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.*;

public class DelHome implements IOneCommand {
    @Override
    public void init() {
        //delhome <home>
        command(DELHOME_LABEL)
                .withPermission(DELHOME_PERMISSION)
                .withAliases(DELHOME_ALIASES)
                .withArguments(new HomeArg0())
                .executesPlayer((p, args) -> {
                    deleteHome(p, p, (String) args[0]);
                }).override();
        //delhome <player> <home>
        command(DELHOME_LABEL)
                .withPermission(DELHOME_PERMISSION)
                .withAliases(DELHOME_ALIASES)
                .withArguments(new HomeArg0(), new HomeArg1())
                .executes((sender, args) -> {
                    OfflinePlayer offP = Bukkit.getOfflinePlayer((String) args[0]);
                    deleteHome(sender, offP, (String) args[1]);
                }).register();
    }

    private void deleteHome(CommandSender sender, OfflinePlayer offP, String home) {
        User target = User.of(offP);
        if (target == null) {
            sender.sendMessage(PLAYER_NEVER_VISITED_SERVER.getString());
        } else {
            boolean isSelf = sender.getName().equals(offP.getName());
            //todo ? if has default home, it uses it, else takes first home from list
            home = home.toLowerCase();
            //todo check if user has home
            target.delHome(home);
            target.sendMessage(DELHOME_DELETED.rp(HOME_PH, home));

            if (!isSelf) {
                sender.sendMessage(DELHOME_OTHER.rp(PLAYER_PH, target.getName(), HOME_PH, home));
            }
        }
    }
}




















