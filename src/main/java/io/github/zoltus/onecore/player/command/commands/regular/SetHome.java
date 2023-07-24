package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class SetHome implements ICommand {

    //todo homelimit test, todo swap  home and player arg
    @Override
    public void init() {
        //sethome <home>
        Argument<?> arg1 = new StringArgument(Lang.NODES_HOME_NAME.getString())
                .executesPlayer((p, args) -> {
                    //This arg can be in arg 0 or 1 position
                    Object arg = args.args().length == 1 ? args.get(0) : args.get(1);
                    setHome(p, p, (String) arg);
                });
        //sethome <home> <player>
        Argument<?> arg0 = new OfflinePlayerArgument()
                .withPermission(Commands.SETHOME_PERMISSION_OTHER.asPermission())
                .executes((sender, args) -> {
                    OfflinePlayer offP = (OfflinePlayer) args.get(0);
                    setHome(sender, offP, (String) args.get(1));
                });
        //sethome
        new Command(Commands.SETHOME_LABEL)
                .withPermission(Commands.SETHOME_PERMISSION)
                .withAliases(Commands.SETHOME_ALIASES)
                .executesPlayer((p, args) -> {
                    setHome(p, p, null);
                })
                .then(arg1)
                .then(arg0.then(arg1))
                .override();
    }

    private void setHome(CommandSender sender, OfflinePlayer offP, String home) {
        User target = User.of(offP);
        if (target == null) {
            sender.sendMessage(Lang.PLAYER_NEVER_VISITED_SERVER.getString());
        } else {
            //Async because sethome scans permissions and if user has lot it could slow down the server.
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                String finalHome = home == null ? Commands.HOME_DEFAULT_NAME.getString() : home.toLowerCase();
                boolean isSelf = sender.getName().equals(offP.getName());
                boolean canHaveMoreHomes = target.hasFreeHomeSlot();
                if ((target.hasHome(finalHome) || canHaveMoreHomes) || !isSelf) {
                    target.setHome(finalHome, target.getPlayer().getLocation());
                    Lang.SETHOME_SET.send(target, IConfig.HOME_PH, finalHome);
                } else {
                    Lang.SETHOME_FULL_HOMES.send(target);
                    return;
                }
                if (!isSelf) {
                    Lang.SETHOME_OTHER.send(sender, IConfig.PLAYER_PH, offP.getName(), IConfig.HOME_PH, finalHome);
                }
            });
        }
    }
}


/*


 */