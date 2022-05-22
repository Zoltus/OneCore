package sh.zoltus.onecore.player.command.commands;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.User;
import sh.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import sh.zoltus.onecore.utils.NBTPlayer;
import sh.zoltus.onecore.utils.PreLocation;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.*;

public class Warp implements IOneCommand {

    @Setter
    @Getter
    private static HashMap<String, PreLocation> warps = new LinkedHashMap<>();

    private Argument warpArg() {
        return new CustomArgument<>(NODES_WARP_NAME.getString(), (info) -> {
            String input = info.input();
            if (!warps.containsKey(input)) {
                throw new CustomArgument.CustomArgumentException(WARP_NOT_SET.rp(LIST_PH, warps.keySet().toString()));
            } else {
                return input;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> toSuggestion(info.currentArg(), warps.keySet().toArray(new String[0]))));
    }

    //todo cleanup
    @Override
    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                //warp, warps
                command(WARP_LABEL)
                        .withPermission(WARP_PERMISSION)
                        .withAliases(WARP_ALIASES)
                        .executesPlayer((p, args) -> {
                    p.sendMessage(WARP_LIST.rp(LIST_PH, warps.keySet().toString()));
                }),

                //warp <warp>
                command(WARP_LABEL)
                        .withPermission(WARP_PERMISSION)
                        .withAliases(WARP_ALIASES)
                        .withArguments(warpArg())
                        .executesPlayer((p, args) -> {
                    String warpName = (String) args[0];
                    PreLocation warp = warps.get(warpName);
                    User user = User.of(p);
                    user.teleportTimer(warp.toLocation());
                }),

                //warp <warp> <player>
                command(WARP_LABEL)
                        .withPermission(WARP_PERMISSION_OTHER)
                        .withAliases(WARP_ALIASES)
                        .withArguments(warpArg(), new OfflinePlayerArgument())
                        .executes((sender, args) -> {
                    String warpName = (String) args[0];
                    OfflinePlayer offTarget = (OfflinePlayer) args[1];
                    Player p = offTarget.getPlayer();
                    Location warp = warps.get(warpName).toLocation();

                    if (p != null) {
                        p.teleport(warp);
                    } else {
                        NBTPlayer nbtPlayer = new NBTPlayer(offTarget);
                        nbtPlayer.setLocation(warp);
                        nbtPlayer.save();
                    }
                    if (offTarget.getPlayer() != sender) {
                        sender.sendMessage(WARP_TARGET_SENT.rp(PLAYER_PH, offTarget.getName(), WARP_PH, warpName));
                    }
                })
        };
    }
}




















