package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.ArgumentTree;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.HomeArg0;
import io.github.zoltus.onecore.player.command.arguments.HomeArg1;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.Command;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class DelHome implements ICommand {
    @Override
    public void init() {
        //delhome <home>
        ArgumentTree arg0 = new HomeArg0()
                .executesPlayer((p, args) -> {
                    deleteHome(p, p, (String) args[0]);
                });
        //delhome <player> <home>
        ArgumentTree arg1 = new HomeArg1()
                .executes((sender, args) -> {
                    OfflinePlayer offP = Bukkit.getOfflinePlayer((String) args[0]);
                    deleteHome(sender, offP, (String) args[1]);
                });
        new Command(DELHOME_LABEL)
                .withPermission(DELHOME_PERMISSION)
                .withAliases(DELHOME_ALIASES)
                .then(arg0.then(arg1))
                .override();
    }

    private void deleteHome(CommandSender sender, OfflinePlayer offP, String home) {
        User target = User.of(offP);
        if (target == null) {
            sender.sendMessage(PLAYER_NEVER_VISITED_SERVER.getString());
        } else {
            boolean isSelf = sender.getName().equals(offP.getName());
            //todo ? if has default home, it uses it, else takes first home from list
            home = home == null ? HOME_DEFAULT_NAME.getString() : home.toLowerCase();
            //todo check if user has home
            target.delHome(home);
            target.sendMessage(DELHOME_DELETED.rp(HOME_PH, home));

            if (!isSelf) {
                sender.sendMessage(DELHOME_OTHER.rp(PLAYER_PH, target.getName(), HOME_PH, home));
            }
        }
    }
}




















