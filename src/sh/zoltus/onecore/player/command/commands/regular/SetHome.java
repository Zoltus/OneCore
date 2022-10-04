package sh.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import sh.zoltus.onecore.player.User;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.*;

public class SetHome implements IOneCommand {

    //todo homelimit test
    @Override
    public void init() {
        //sethome
        command(SETHOME_LABEL)
                .withPermission(SETHOME_PERMISSION)
                .withAliases(SETHOME_ALIASES)
                .executesPlayer((p, args) -> {
                    setHome(p, p, null);
                }).override();
        //sethome <home>
        command(SETHOME_LABEL)
                .withPermission(SETHOME_PERMISSION)
                .withAliases(SETHOME_ALIASES)
                .withArguments(new StringArgument(NODES_HOME_NAME.getString()))
                .executesPlayer((p, args) -> {
                    setHome(p, p, (String) args[0]);
                }).register();
        //sethome <home> <player> //todo messages
        command(SETHOME_LABEL)
                .withPermission(SETHOME_PERMISSION_OTHER)
                .withAliases(SETHOME_ALIASES)
                .withArguments(new StringArgument(NODES_HOME_NAME.getString()))
                .withArguments(new OfflinePlayerArgument())
                .executes((sender, args) -> {
                    OfflinePlayer offP = Bukkit.getOfflinePlayer((String) args[0]);
                    setHome(sender, offP, (String) args[1]);
                }).register();
    }

    private void setHome(CommandSender sender, OfflinePlayer offP, String home) {
        User target = User.of(offP);
        if (target == null) {
            sender.sendMessage(PLAYER_NEVER_VISITED_SERVER.getString());
        } else {
            home = home.toLowerCase();
            boolean isSelf = sender.getName().equals(offP.getName());
            boolean canHaveMoreHomes = isSelf || target.hasFreeHomeSlots();
            if (target.hasHome(home) || canHaveMoreHomes) {
                target.setHome(home, target.getPlayer().getLocation());
                target.sendMessage(SETHOME_SET.rp(HOME_PH, home));
            } else {
                target.sendMessage(SETHOME_FULL_HOMES.getString());
                return;
            }
            if (!isSelf) {
                sender.sendMessage(SETHOME_OTHER.rp(PLAYER_PH, target.getName(), HOME_PH, home));
            }
        }
    }
}


/*


 */