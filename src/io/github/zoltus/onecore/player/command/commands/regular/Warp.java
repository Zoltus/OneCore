package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.data.configuration.Yamls;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import io.github.zoltus.onecore.data.configuration.OneYml;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.nbt.NBTPlayer;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class Warp implements ICommand {

    private final OneYml warps = Yamls.WARPS.getYml();

    record WarpObj(String name, Location location) {
    }

    private Argument<?> warpArg() {
        return new CustomArgument<>(new StringArgument(NODES_WARP_NAME.getString()), (info) -> {
            String input = info.input();
            Location warp = warps.getLocation(input);
            if (warp == null) {
                throw new CustomArgument.CustomArgumentException(WARP_NOT_FOUND.rp(LIST_PH, warps.getKeys(false)));
            } else {
                return new WarpObj(input, warp);
            }
        }).replaceSuggestions(ArgumentSuggestions
                .strings(info -> toSuggestion(info.currentArg(), warps.getKeys(false)
                        .toArray(new String[0]))));
    }

    //todo cleanup
    @Override
    public void init() {
        //warp <warp>
        ArgumentTree arg0 = warpArg()
                .executesPlayer((p, args) -> {
                    WarpObj warp = (WarpObj) args[0];
                    User user = User.of(p);
                    user.teleportTimer(warp.location());
                });
        //warp <warp> <player>
        ArgumentTree arg1 = new OfflinePlayerArgument()
                .executes((sender, args) -> {
                    WarpObj warp = (WarpObj) args[0];
                    String warpName = warp.name();
                    OfflinePlayer offTarget = (OfflinePlayer) args[1];
                    Player p = offTarget.getPlayer();
                    Location loc = warp.location();
                    if (p != null) {
                        p.teleport(loc);
                    } else {
                        NBTPlayer nbtPlayer = new NBTPlayer(offTarget);
                        nbtPlayer.setLocation(loc);
                        nbtPlayer.save();
                    }
                    if (offTarget.getPlayer() != sender) {
                        sender.sendMessage(WARP_TARGET_SENT.rp(PLAYER_PH, offTarget.getName(), WARP_PH, warpName));
                    }
                });
        //warp, warps
        new Command(WARP_LABEL)
                .withPermission(WARP_PERMISSION)
                .withAliases(WARP_ALIASES)
                .executesPlayer((p, args) -> {
                    p.sendMessage(WARP_LIST.rp(LIST_PH, warps.getKeys(false).toString()));
                }).then(arg0.then(arg1))
                .override();
    }
}




















