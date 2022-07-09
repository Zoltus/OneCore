package sh.zoltus.onecore.player.command.commands.admin;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import sh.zoltus.onecore.player.nbt.NBTPlayer;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.*;


public class Tp implements IOneCommand {
    @Override
    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                //tphere <player>
                command(TPHERE_LABEL)
                        .withPermission(TPHERE_PERMISSION)
                        .withAliases(TPHERE_ALIASES)
                        .withArguments(new OfflinePlayerArgument())
                        .executesPlayer((sender, args) -> {
                    OfflinePlayer offlineTarget = (OfflinePlayer) args[0];
                    tp(offlineTarget, sender.getLocation());
                    if (offlineTarget.isOnline()) {
                        sender.sendMessage(TPHERE_OFFLINE_TARGET.rp(PLAYER_PH, offlineTarget.getName()));
                    }
                }),
                //tp <player>
                command(TP_LABEL)
                        .withPermission(TP_PERMISSION)
                        .withAliases(TP_ALIASES)
                        .withArguments(new OfflinePlayerArgument())
                        .executesPlayer((sender, args) -> {
                    OfflinePlayer offlineTarget = (OfflinePlayer) args[0];
                    Location destination = getLoc(offlineTarget);
                    sender.teleport(destination);
                    sender.sendMessage(TP_OFFLINE_TARGET.rp(PLAYER_PH, offlineTarget.getName()));
                }),
                //tp <player> <player>
                command(TP_LABEL)
                        .withPermission(TP_PERMISSION_OTHER)
                        .withAliases(TP_ALIASES)
                        .withArguments(new OfflinePlayerArgument())
                        .withArguments(new OfflinePlayerArgument("2"))
                        .executes((sender, args) -> {
                    OfflinePlayer fromOff = (OfflinePlayer) args[0];
                    OfflinePlayer target = (OfflinePlayer) args[1];
                    Location destination = getLoc(target);
                    tp(fromOff, destination);
                    if (!fromOff.isOnline() || !target.isOnline()) {
                        sender.sendMessage(TP_OFFLINE_TARGETS.getString());
                    }
                }),
        };
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
