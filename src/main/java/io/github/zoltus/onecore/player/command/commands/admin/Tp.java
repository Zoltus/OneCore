package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LocationArgument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import io.github.zoltus.onecore.player.nbt.NBTPlayer;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.zoltus.onecore.data.configuration.PlaceHolder.PLAYER2_PH;
import static io.github.zoltus.onecore.data.configuration.PlaceHolder.PLAYER_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Commands.TP_PERMISSION_OTHER;

public class Tp implements ICommand {
    @Override
    public void init() {
        //tp <player>
        Argument<?> arg0 = new OfflinePlayerArgument("1")
                .executesPlayer((sender, args) -> {
                    OfflinePlayer offlineTarget = (OfflinePlayer) args.get(0);
                    Location destination = getLoc(offlineTarget);
                    sender.teleport(destination);
                    if (offlineTarget.isOnline()) {
                        Lang.TP_TELEPORTED_TARGET.rb(PLAYER_PH, offlineTarget.getName()).send(sender);
                    } else {
                        Lang.TP_TELEPORTED_OFFLINE_TARGET.rb(PLAYER_PH, offlineTarget.getName()).send(sender);
                    }
                });
        //tp <player> <player>
        Argument<?> arg1 = new OfflinePlayerArgument("2")
                .withPermission(TP_PERMISSION_OTHER.asPermission())
                .executes((sender, args) -> {
                    OfflinePlayer fromOff = (OfflinePlayer) args.get(0);
                    OfflinePlayer target = (OfflinePlayer) args.get(1);
                    Location destination = getLoc(target);
                    tp(sender, fromOff, destination);
                    if (fromOff.isOnline() && target.isOnline()) {
                        Lang.TP_TELEPORTED_TARGETS.rb(PLAYER_PH, fromOff.getName())
                                .rb(PLAYER2_PH, target.getName())
                                .send(sender);
                    } else {
                        Lang.TP_TELEPORTED_OFFLINE_TARGETS.rb(PLAYER_PH, fromOff.getName())
                                .rb(PLAYER2_PH, target.getName())
                                .send(sender);
                    }
                });
        //tp <coord> <player>
        Argument<?> locArg2 = new OfflinePlayerArgument("2")
                .withPermission(TP_PERMISSION_OTHER.asPermission())
                .withPermission("")
                .executes((sender, args) -> {
                    Location destination = (Location) args.get(0);
                    OfflinePlayer offTarget = (OfflinePlayer) args.get(1);
                    tp(sender, offTarget, destination);
                });

        //tp <coord>
        Argument<?> locArg = new LocationArgument("loc")
                .executesPlayer((sender, args) -> {
                    Location destination = (Location) args.get(0);
                    tp(sender, sender, destination);
                }).then(locArg2);
        //tp
        new Command(Commands.TP_LABEL)
                .withPermission(Commands.TP_PERMISSION)
                .withAliases(Commands.TP_ALIASES)
                .then(arg0.then(arg1))
                .then(locArg)
                .override();
        //tphere <player>
        new Command(Commands.TPHERE_LABEL)
                .withPermission(Commands.TPHERE_PERMISSION)
                .withAliases(Commands.TPHERE_ALIASES)
                .then(new OfflinePlayerArgument()
                        .executesPlayer((sender, args) -> {
                            OfflinePlayer offlineTarget = (OfflinePlayer) args.get(0);
                            tp(sender, offlineTarget, sender.getLocation());
                            if (offlineTarget.isOnline()) {
                                Lang.TPHERE_TELEPORTED.rb(PLAYER_PH, offlineTarget.getName())
                                        .send(sender);
                            } else {
                                Lang.TPHERE_OFFLINE_TARGET.rb(PLAYER_PH, offlineTarget.getName())
                                        .send(sender);
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

    private void tp(CommandSender sender, OfflinePlayer offTarget, Location loc) {
        Player target = offTarget.getPlayer();
        if (target != null) {
            target.setVelocity(target.getVelocity().zero());
            target.teleport(loc);
        } else {
            NBTPlayer nbtPlayer = new NBTPlayer(offTarget);
            nbtPlayer.setLocation(loc);
            nbtPlayer.save();
        }
    }
}
