package sh.zoltus.onecore.player.home;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import sh.zoltus.onecore.player.command.User;
import sh.zoltus.onecore.player.teleporting.PreLocation;

import java.util.Objects;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.*;

public class HomeHandler {

    //todo cleanup
    public static void handle(CommandSender sender, OfflinePlayer offP, String home, HomeAction act) {
        User target = User.ofNullable(offP);
        if (target != null) {
            //todo ? if has default home, it uses it, else takes first home from list
            home = home == null ? HOME_DEFAULT_NAME.getString().toLowerCase() : home.toLowerCase();

            String targetMsg = "";
            switch (act) {
                case DELETE -> {
                    //todo check if user has home
                    target.delHome(home);
                    target.sendMessage(DELHOME_DELETED.rp(HOME_PH, home));
                    targetMsg = DELHOME_OTHER.rp(PLAYER_PH, target.getName(), HOME_PH, home);
                }
                case SET -> {
                    User user = User.of(Objects.requireNonNull(Bukkit.getPlayer(sender.getName())));
                    if (user.hasHome(home) || user.hasHomeSlots()) {
                        user.setHome(home, user.getPlayer().getLocation());
                        user.sendMessage(SETHOME_SET.rp(HOME_PH, home));
                    } else {
                        user.sendMessage(SETHOME_FULL_HOMES.getString());
                        return;
                    }
                    targetMsg = SETHOME_OTHER.rp(PLAYER_PH, target.getName(), HOME_PH, home);
                }
                case HOME -> {
                    PreLocation loc = target.getHome(home);
                    User user = User.of(Objects.requireNonNull(Bukkit.getPlayer(sender.getName())));
                    if (loc != null) {
                        user.teleportTimer(loc.toLocation());
                    } else {
                        sender.sendMessage(HOME_LIST.rp(LIST_PH, user.getHomes().keySet()));
                        return;
                    }
                    targetMsg = HOME_SENT_TARGET.rp(PLAYER_PH, target.getName(), HOME_PH, home);
                }
            }

            if (target.getPlayer() != sender) {
                sender.sendMessage(targetMsg);
            }
        } else {
            sender.sendMessage(PLAYER_NEVER_VISITED_SERVER.getString());
        }
    }

    //private static void getHome
}

