package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.data.configuration.OneYml;
import io.github.zoltus.onecore.data.configuration.Yamls;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import io.github.zoltus.onecore.player.teleporting.LocationUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.nbt.NBTPlayer;

import static io.github.zoltus.onecore.data.configuration.PlaceHolder.PLAYER_PH;

public class Spawn implements ICommand {

    //todo improve & cleanup
    public static Location getSpawn() {
        return Yamls.CONFIG.getYml().getLocation("Data.spawn");
    }

    public static Location getFirstJoinSpawn() {
        return Yamls.CONFIG.getYml().getLocation("Data.first-join-spawn");
    }

    public static void setFirstJoinSpawn(Location location) {
        OneYml yml = Yamls.CONFIG.getYml();
        yml.set("Data.first-join-spawn", location);
        yml.save();
        yml.reload();
    }

    public static void setSpawn(Location location) {
        OneYml yml = Yamls.CONFIG.getYml();
        yml.set("Data.spawn", location);
        yml.save();
        yml.reload();
    }

    @Override
    public void init() {
        //spawn <player>
        Argument<?> arg0 = new OfflinePlayerArgument()
                .withPermission(Commands.SPAWN_PERMISSION_OTHER.asPermission())
                .executes((sender, args) -> {
                    Location spawn = getSpawn();
                    if (spawn == null) {
                        Lang.SPAWN_IS_NOT_SET.send(sender);
                    } else {
                        OfflinePlayer offTarget = (OfflinePlayer) args.get(0);
                        Player p = offTarget.getPlayer();
                        if (p != null) {
                            LocationUtils.teleportWMountSafeAsync(p, spawn);
                        } else {
                            NBTPlayer nbtPlayer = new NBTPlayer(offTarget);
                            nbtPlayer.setLocation(spawn);
                            nbtPlayer.save();
                        }
                        if (offTarget.getPlayer() != sender) {
                            Lang.SPAWN_TARGET_SENT.rb(PLAYER_PH, offTarget.getName()).send(sender);
                        }
                    }
                });
        //spawn
        new Command(Commands.SPAWN_LABEL)
                .withPermission(Commands.SPAWN_PERMISSION)
                .withAliases(Commands.SPAWN_ALIASES)
                .executesPlayer((player, args) -> {
                    User user = User.of(player);
                    Location spawn = getSpawn();
                    if (spawn == null) {
                        Lang.SPAWN_IS_NOT_SET.send(user);
                    } else {
                        user.teleport(spawn);
                    }
                }).then(arg0)
                .override();
    }
}
