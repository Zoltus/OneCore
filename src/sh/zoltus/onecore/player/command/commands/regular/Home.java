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
                    handle(p, User.of(p), null, Action.HOME);
                }).override();
        //home <home>
        command(HOME_LABEL)
                .withPermission(HOME_PERMISSION)
                .withAliases(HOME_ALIASES)
                .withArguments(new HomeArg0()) //String
                .executesPlayer((p, args) -> {
                    handle(p, User.of(p), (String) args[0], Action.HOME);
                }).register();
        //home <player> <home>
        command(HOME_LABEL)
                .withPermission(HOME_PERMISSION)
                .withAliases(HOME_ALIASES)
                .withArguments(new HomeArg0(), new HomeArg1())
                .executes((sender, args) -> {
                    OfflinePlayer offP = Bukkit.getOfflinePlayer((String) args[0]);
                    handle(sender, User.of(offP), (String) args[1], Action.HOME);
                }).register();
    }

    enum Action {
        DELETE, SET, HOME
    }

    static void handle(CommandSender sender, User target, String home, Action action) {
        if (target == null) {
            sender.sendMessage(PLAYER_NEVER_VISITED_SERVER.getString());
        } else {
            //todo ? if has default home, it uses it, else takes first home from list
            home = home.toLowerCase();
            String message = "";
            switch (action) {
                case DELETE -> {
                    //todo check if user has home
                    target.delHome(home);
                    target.sendMessage(DELHOME_DELETED.rp(HOME_PH, home));
                    message = DELHOME_OTHER.rp(PLAYER_PH, target.getName(), HOME_PH, home);
                }
                case SET -> {
                    if (target.hasHome(home) || target.hasHomeSlots()) {
                        target.setHome(home, target.getPlayer().getLocation());
                        target.sendMessage(SETHOME_SET.rp(HOME_PH, home));
                    } else {
                        target.sendMessage(SETHOME_FULL_HOMES.getString());
                        return;
                    }
                    message = SETHOME_OTHER.rp(PLAYER_PH, target.getName(), HOME_PH, home);
                }
                case HOME -> { //todo default to homes 0
                    PreLocation loc = target.getHome(home);
                    User user = User.of((Player) sender); //Cant be other than player
                    //HOME_DEFAULT_NAME.getString().toLowerCase()
                    if (loc != null) {
                        user.teleportTimer(loc.toLocation());
                    } else {
                        sender.sendMessage(HOME_LIST.rp(LIST_PH, target.getHomes().keySet()));
                        return;
                    }
                    message = HOME_TELEPORT_OTHERS.rp(PLAYER_PH, target.getName(), HOME_PH, home);
                }
            }
            if (!sender.equals(target.getPlayer())) {
                sender.sendMessage(message);
            }
        }
    }
}




















