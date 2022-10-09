package sh.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.ArgumentTree;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.Command;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import sh.zoltus.onecore.player.nbt.NBTPlayer;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.*;

public class Tp implements IOneCommand {
    @Override
    public void init() {
        //tp <player>
        ArgumentTree arg0 = new OfflinePlayerArgument()
                .executesPlayer((sender, args) -> {
                    OfflinePlayer offlineTarget = (OfflinePlayer) args[0];
                    Location destination = getLoc(offlineTarget);
                    sender.teleport(destination);
                    if (offlineTarget.isOnline()) {
                        sender.sendMessage(TP_TELEPORTED_TARGET.rp(PLAYER_PH, offlineTarget.getName()));
                    } else {
                        sender.sendMessage(TP_TELEPORTED_OFFLINE_TARGET.rp(PLAYER_PH, offlineTarget.getName()));
                    }
                });
        //tp <player> <player>
        ArgumentTree arg1 = new OfflinePlayerArgument("2")
                .executes((sender, args) -> {
                    OfflinePlayer fromOff = (OfflinePlayer) args[0];
                    OfflinePlayer target = (OfflinePlayer) args[1];
                    Location destination = getLoc(target);
                    tp(fromOff, destination);
                    if (fromOff.isOnline() && target.isOnline()) {
                        sender.sendMessage(TP_TELEPORTED_TARGETS.rp(PLAYER_PH, fromOff.getName(), PLAYER2_PH, target.getName()));
                    } else {
                        sender.sendMessage(TP_TELEPORTED_OFFLINE_TARGETS.rp(PLAYER_PH, fromOff.getName(), PLAYER2_PH, target.getName()));
                    }
                });
        //tp
        new Command(TP_LABEL)
                .withPermission(TP_PERMISSION_OTHER)
                .withAliases(TP_ALIASES)
                .then(arg0.then(arg1))
                .override();
        //tphere <player>
        new Command(TPHERE_LABEL)
                .withPermission(TPHERE_PERMISSION)
                .withAliases(TPHERE_ALIASES)
                .then(new OfflinePlayerArgument()
                        .executesPlayer((sender, args) -> {
                            OfflinePlayer offlineTarget = (OfflinePlayer) args[0];
                            tp(offlineTarget, sender.getLocation());
                            if (offlineTarget.isOnline()) {
                                sender.sendMessage(TPHERE_TELEPORTED.rp(PLAYER_PH, offlineTarget.getName()));
                            } else {
                                sender.sendMessage(TPHERE_OFFLINE_TARGET.rp(PLAYER_PH, offlineTarget.getName()));
                            }
                        })).override();
    }

    private Location getLoc(OfflinePlayer offTarget) {
        Player target = offTarget.getPlayer();
        if (target != null) {
            return target.getLocation();
        } else {
            NBTPlayer nbtPlayer = new NBTPlayer(offTarget);
            return nbtPlayer.getLocation();
        }
    }

    private void tp(OfflinePlayer offTarget, Location loc) {
        Player target = offTarget.getPlayer();
        if (target != null) {
            target.teleport(loc);
        } else {
            NBTPlayer nbtPlayer = new NBTPlayer(offTarget);
            nbtPlayer.setLocation(loc);
            nbtPlayer.save();
        }
    }
}
