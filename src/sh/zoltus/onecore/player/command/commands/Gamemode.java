package sh.zoltus.onecore.player.command.commands;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.configuration.yamls.Commands;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import sh.zoltus.onecore.utils.NBTPlayer;

import java.util.Arrays;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static sh.zoltus.onecore.configuration.yamls.Commands.MODE_PH;
import static sh.zoltus.onecore.configuration.yamls.Commands.PLAYER_PH;
import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Config.PERMISSION_PREFIX;
import static sh.zoltus.onecore.configuration.yamls.Lang.*;

public class Gamemode implements IOneCommand {

    private Argument gamemodeArgument() {
        return new CustomArgument<>(NODES_GAMEMODE.getString(), (info) -> {
            GameMode gm = getGamemode(info.input());
            if (gm == null) {
                throw new CustomArgument.CustomArgumentException(GAMEMODE_INVALID_GAMEMODE.getString());
            } else if (!info.sender().hasPermission(GAMEMODE_MODE_PERMISSION.getAsPermission() + gm.name().toLowerCase())) {
                throw new CustomArgument.CustomArgumentException(GAMEMODE_MODE_PERMISSION_MISSING.rp(MODE_PH, PERMISSION_PREFIX.getAsPermission() + gm.name().toLowerCase()));
            } else {
                return gm;
            }
        }).replaceSuggestions((info) -> toSuggestion(info.currentArg(), GAMEMODE_SUGGESTIONS.getSplitArr()));
    }

    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                //gamemode creative
                command(GAMEMODE_LABEL)
                        .withPermission(GAMEMODE_PERMISSION)
                        .withAliases(GAMEMODE_ALIASES)
                        .withArguments(gamemodeArgument())
                        .executesPlayer((player, args) -> {
                    setOnlineGamemode(player, (GameMode) args[0]);
                }),
                //gamemode creative <player>
                command(GAMEMODE_LABEL)
                        .withPermission(GAMEMODE_OTHER_PERMISSION)
                        .withAliases(GAMEMODE_ALIASES)
                        .withArguments(gamemodeArgument(), new OfflinePlayerArgument())
                        .executes((sender, args) -> {
                    handleTarget(sender, (OfflinePlayer) args[1], (GameMode) args[0]);
                }),
        };
    }

    private void handleTarget(CommandSender sender, OfflinePlayer offTarget, GameMode gm) {
        Player target = offTarget.getPlayer();
        //todo hasPlayedBefore
        if (target != null && target == sender) {
            setOnlineGamemode(target, gm);
            return;
        }

        boolean hadAlready;
        //Change gamemodes
        if (target != null) {
            hadAlready = target.getGameMode() == gm;
            setOnlineGamemode(target, gm);
        } else {
            NBTPlayer nbtPlayer = new NBTPlayer(offTarget);
            int currentGamemode = nbtPlayer.getplayerGameType();
            int newGamemodeValue = gm.getValue();
            nbtPlayer.setPreviousPlayerGameType(currentGamemode);
            nbtPlayer.setplayerGameType(newGamemodeValue);
            hadAlready = (currentGamemode == newGamemodeValue);
            nbtPlayer.save();
        }

        if (hadAlready) {
            sender.sendMessage(GAMEMODE_TARGET_ALREADY_IN_GAMEMODE.rp(MODE_PH, gm.name(), PLAYER_PH, offTarget.getName()));
        } else {
            sender.sendMessage(GAMEMODE_TARGETS_GAMEMODE_CHANGED.rp(MODE_PH, gm.name(), PLAYER_PH, offTarget.getName()));
        }
    }

    private void setOnlineGamemode(Player player, GameMode gm) {
        if (player.getGameMode() != gm) {
            player.setGameMode(gm);
            player.sendMessage(GAMEMODE_CHANGED.rp(MODE_PH, gm.name()));
        }
    }

    private GameMode getGamemode(String input) {
        Predicate<Commands> hasGm = (gamemodes) -> Arrays
                .stream(gamemodes.getString().split(",")).anyMatch(input::equalsIgnoreCase);
        if (hasGm.test(GAMEMODE_ALIASES_SURVIVAL)) {
            return GameMode.SURVIVAL;
        } else if (hasGm.test(GAMEMODE_ALIASES_CREATIVE)) {
            return GameMode.CREATIVE;
        } else if (hasGm.test(GAMEMODE_ALIASES_ADVENTURE)) {
            return GameMode.ADVENTURE;
        } else if (hasGm.test(GAMEMODE_ALIASES_SPECTATOR)) {
            return GameMode.SPECTATOR;
        } else {
            return null;
        }
    }

}
