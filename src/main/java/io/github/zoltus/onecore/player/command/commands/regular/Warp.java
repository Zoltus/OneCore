package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.data.configuration.Yamls;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import io.github.zoltus.onecore.player.command.arguments.WarpArgument;
import io.github.zoltus.onecore.player.nbt.NBTPlayer;
import io.github.zoltus.onecore.player.teleporting.LocationUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Set;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.WARP_LIST;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.WARP_TARGET_SENT;

public class Warp implements ICommand {

    public record WarpObj(String name, Location location) {
    }

    //todo cleanup
    @Override
    public void init() {
        //warp <warp>
        Argument<?> arg0 = new WarpArgument()
                .executesPlayer((p, args) -> {
                    WarpObj warp = (WarpObj) args.get(0);
                    User user = User.of(p);
                    user.teleport(warp.location());
                });
        //warp <warp> <player>
        Argument<?> arg1 = new OfflinePlayerArgument()
                .withPermission(Commands.WARP_PERMISSION_OTHER.asPermission())
                .executes((sender, args) -> {
                    WarpObj warp = (WarpObj) args.get(0);
                    String warpName = warp.name();
                    OfflinePlayer offTarget = (OfflinePlayer) args.get(1);
                    Player p = offTarget.getPlayer();
                    Location loc = warp.location();
                    if (p != null) {
                        LocationUtils.teleportSafeAsync(p, loc);
                    } else {
                        NBTPlayer nbtPlayer = new NBTPlayer(offTarget);
                        nbtPlayer.setLocation(loc);
                        nbtPlayer.save();
                    }
                    if (offTarget.getPlayer() != sender) {
                        WARP_TARGET_SENT.send(sender, WARP_PH, warpName, PLAYER_PH, offTarget.getName());
                    }
                });
        //warp, warps
        new Command(WARP_LABEL)
                .withPermission(WARP_PERMISSION)
                .withAliases(WARP_ALIASES)
                .executesPlayer((p, args) -> {
                    WARP_LIST.send(p, LIST_PH, getWarps().toString());
                }).then(arg0.then(arg1))
                .override();
    }

    public static Set<String> getWarps() {
        return Yamls.WARPS.getYml().getKeys(false);
    }
}




















