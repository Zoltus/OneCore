package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.HomeArg0;
import io.github.zoltus.onecore.player.command.arguments.HomeArg1;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import static io.github.zoltus.onecore.data.configuration.PlaceHolder.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class DelHome implements ICommand {
    @Override
    public void init() {
        //delhome <home>
        Argument<?> arg0 = new HomeArg0()
                .executesPlayer((p, args) -> {
                    deleteHome(p, p, (String) args.get(0));
                });
        //delhome <player> <home>
        Argument<?> arg1 = new HomeArg1()
                .executes((sender, args) -> {
                    OfflinePlayer offP = Bukkit.getOfflinePlayer((String) args.get(0));
                    deleteHome(sender, offP, (String) args.get(1));
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
            PLAYER_NEVER_VISITED_SERVER.send(sender);
        } else {
            boolean isSelf = sender.getName().equals(offP.getName());
            if (target.hasHome(home)) {
                target.delHome(home);
                DELHOME_DELETED.rb(HOME_PH, home).send(target);
                if (!isSelf) {
                    DELHOME_OTHER.rb(PLAYER_PH, target.getName()).rb(HOME_PH, home).send(sender);
                }
            } else {
                Lang.HOME_LIST.rb(LIST_PH, target.getHomes().keySet()).send(sender);
            }
        }
    }
}




















