package sh.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.ArgumentTree;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.data.configuration.OneYml;
import sh.zoltus.onecore.data.configuration.Yamls;
import sh.zoltus.onecore.player.User;
import sh.zoltus.onecore.player.command.Command;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import sh.zoltus.onecore.player.nbt.NBTPlayer;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.SPAWN_IS_NOT_SET;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.SPAWN_TARGET_SENT;

public class Spawn implements IOneCommand {

    private static final OneYml config = Yamls.CONFIG.getYml();

    public static Location getSpawn() {
        return config.getLocation("spawn");
    }

    public static void setSpawn(Location location) {
        config.set("Data.spawn", location);
        config.save();
        config.reload();
    }

    @Override
    public void init() {
        //spawn <player>
        ArgumentTree arg0 = new OfflinePlayerArgument()
                .executes((sender, args) -> {
                    Location spawn = getSpawn();
                    if (spawn == null) {
                        sender.sendMessage(SPAWN_IS_NOT_SET.getString());
                    } else {
                        OfflinePlayer offTarget = (OfflinePlayer) args[0];
                        Player p = offTarget.getPlayer();
                        if (p != null) {
                            p.teleport(spawn);
                        } else {
                            NBTPlayer nbtPlayer = new NBTPlayer(offTarget);
                            nbtPlayer.setLocation(spawn);
                            nbtPlayer.save();
                        }
                        if (offTarget.getPlayer() != sender) {
                            sender.sendMessage(SPAWN_TARGET_SENT.rp(PLAYER_PH, offTarget.getName()));
                        }
                    }
                });
        //spawn
        new Command(SPAWN_LABEL)
                .withPermission(SPAWN_PERMISSION)
                .withAliases(SPAWN_ALIASES)
                .executesPlayer((player, args) -> {
                    User user = User.of(player);
                    Location spawn = getSpawn();
                    if (spawn == null) {
                        user.sendMessage(SPAWN_IS_NOT_SET.getString());
                    } else {
                        user.teleportTimer(spawn);
                    }
                }).then(arg0)
                .override();
    }
}
