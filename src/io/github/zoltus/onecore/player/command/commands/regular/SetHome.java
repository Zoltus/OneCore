package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.IOneCommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import io.github.zoltus.onecore.player.User;

public class SetHome implements IOneCommand {

    //todo homelimit test
    @Override
    public void init() {
        //sethome <home>
        ArgumentTree arg0 = new StringArgument(Lang.NODES_HOME_NAME.getString())
                .executesPlayer((p, args) -> {
                    setHome(p, p, (String) args[0]);
                });
        //sethome <home> <player> //todo messages
        ArgumentTree arg1 = new OfflinePlayerArgument()
                .executes((sender, args) -> {
                    OfflinePlayer offP = Bukkit.getOfflinePlayer((String) args[0]);
                    setHome(sender, offP, (String) args[1]);
                });
        //sethome
        new Command(Commands.SETHOME_LABEL)
                .withPermission(Commands.SETHOME_PERMISSION)
                .withAliases(Commands.SETHOME_ALIASES)
                .executesPlayer((p, args) -> {
                    setHome(p, p, null);
                }).then(arg0.then(arg1))
                .override();
    }

    private void setHome(CommandSender sender, OfflinePlayer offP, String home) {
        User target = User.of(offP);
        if (target == null) {
            sender.sendMessage(Lang.PLAYER_NEVER_VISITED_SERVER.getString());
        } else {
            home = home == null ? Commands.HOME_DEFAULT_NAME.getString() : home.toLowerCase();
            boolean isSelf = sender.getName().equals(offP.getName());
            boolean canHaveMoreHomes = isSelf || target.hasFreeHomeSlots();
            if (target.hasHome(home) || canHaveMoreHomes) {
                target.setHome(home, target.getPlayer().getLocation());
                target.sendMessage(Lang.SETHOME_SET.rp(IConfig.HOME_PH, home));
            } else {
                target.sendMessage(Lang.SETHOME_FULL_HOMES.getString());
                return;
            }
            if (!isSelf) {
                sender.sendMessage(Lang.SETHOME_OTHER.rp(IConfig.PLAYER_PH, target.getName(), IConfig.HOME_PH, home));
            }
        }
    }
}


/*


 */