package sh.zoltus.onecore.player.command.commands.regular;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import sh.zoltus.onecore.player.nbt.NBTPlayer;
import sh.zoltus.onecore.utils.PreLocation;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.SPAWN_IS_NOT_SET;
import static sh.zoltus.onecore.configuration.yamls.Lang.SPAWN_TARGET_SENT;

public class Spawn implements IOneCommand {

    @Setter
    @Getter
    private static PreLocation spawn;

    @Override
    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                //spawn
                command(SPAWN_LABEL)
                        .withPermission(SPAWN_PERMISSION)
                        .withAliases(SPAWN_ALIASES)
                        .executesUser((user, args) -> {
                    if (spawn == null) {
                        user.sendMessage(SPAWN_IS_NOT_SET.getString());
                    } else {
                        user.teleportTimer(spawn.toLocation());
                    }
                }),
                //spawn <player>
                command(SPAWN_LABEL)
                        .withPermission(SPAWN_PERMISSION_OTHER)
                        .withAliases(SPAWN_ALIASES)
                        .withArguments(new OfflinePlayerArgument())
                        .executes((sender, args) -> {
                    if (spawn == null) {
                        sender.sendMessage(SPAWN_IS_NOT_SET.getString());
                    } else {
                        OfflinePlayer offTarget = (OfflinePlayer) args[0];
                        Player p = offTarget.getPlayer();
                        Location loc = spawn.toLocation();
                        if (p != null) {
                            p.teleport(loc);
                        } else {
                            NBTPlayer nbtPlayer = new NBTPlayer(offTarget);
                            nbtPlayer.setLocation(loc);
                            nbtPlayer.save();
                        }
                        if (offTarget.getPlayer() != sender) {
                            sender.sendMessage(SPAWN_TARGET_SENT.rp(PLAYER_PH, offTarget.getName()));
                        }
                    }
                })
        };
    }
}
