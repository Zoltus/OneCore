package sh.zoltus.onecore.player.command.commands.regular;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.User;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.HomeArg0;
import sh.zoltus.onecore.player.command.arguments.HomeArg1;
import sh.zoltus.onecore.player.teleporting.PreLocation;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.*;

public class Home implements IOneCommand {

    @Override
    public void init() {
        //home
        command(HOME_LABEL)
                .withPermission(HOME_PERMISSION)
                .withAliases(HOME_ALIASES)
                .executesPlayer((p, args) -> {
                    teleportHome(p, p, null);
                }).override();
        //home <home>
        command(HOME_LABEL)
                .withPermission(HOME_PERMISSION)
                .withAliases(HOME_ALIASES)
                .withArguments(new HomeArg0()) //String
                .executesPlayer((p, args) -> {
                    teleportHome(p, p, (String) args[0]);
                }).register();
        //home <player> <home>
        command(HOME_LABEL)
                .withPermission(HOME_PERMISSION)
                .withAliases(HOME_ALIASES)
                .withArguments(new HomeArg0(), new HomeArg1())
                .executes((sender, args) -> {
                    OfflinePlayer offP = Bukkit.getOfflinePlayer((String) args[0]);
                    teleportHome(sender, offP, (String) args[1]);
                }).register();
    }

    private void teleportHome(CommandSender sender, OfflinePlayer offP, String home) {
        User target = User.of(offP);
        if (target == null) {
            sender.sendMessage(PLAYER_NEVER_VISITED_SERVER.getString());
        } else {
            home = home.toLowerCase();
            PreLocation loc = target.getHome(home);
            User user = User.of((Player) sender); //Cant be other than player since u cant tele others to their homes
            if (loc != null) {
                user.teleportTimer(loc.toLocation());
            } else {
                sender.sendMessage(HOME_LIST.rp(LIST_PH, target.getHomes().keySet()));
                return;
            }
            boolean isSelf = sender.getName().equals(offP.getName());
            if (!isSelf) {
                sender.sendMessage(HOME_TELEPORT_OTHERS.rp(PLAYER_PH, target.getName(), HOME_PH, home));
            }
        }
    }
}




















