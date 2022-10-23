package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.IOneCommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.nbt.NBTPlayer;

import java.util.Arrays;
import java.util.function.Predicate;

import static org.bukkit.GameMode.SURVIVAL;

public class Gamemode implements IOneCommand {

    private Argument<?> gamemodeArgument() {
        return new CustomArgument<>(new StringArgument(Lang.NODES_GAMEMODE.getString()), (info) -> {
            GameMode gm = getGamemode(info.input());
            if (gm == null) {
                throw new CustomArgument.CustomArgumentException(Lang.GAMEMODE_INVALID_GAMEMODE.getString());
            } else if (!info.sender().hasPermission(Commands.GAMEMODE_MODE_PERMISSION.asPermission() + gm.name().toLowerCase())) {
                throw new CustomArgument.CustomArgumentException(Lang.GAMEMODE_MODE_PERMISSION_MISSING.rp(IConfig.MODE_PH, Config.PERMISSION_PREFIX.asPermission() + gm.name().toLowerCase()));
            } else {
                return gm;
            }
        }).replaceSuggestions(ArgumentSuggestions
                .strings((info) -> toSuggestion(info.currentArg(), Commands.GAMEMODE_SUGGESTIONS.getAsArray())));
    }

    @Override
    public void init() {
        //gamemode creative
        ArgumentTree arg0 = gamemodeArgument()
                .executesPlayer((player, args) -> {
                    GameMode gm = (GameMode) args[0];
                    String gmName = getGmName(gm);
                    player.setGameMode(gm);
                    player.sendMessage(Lang.GAMEMODE_CHANGED.rp(IConfig.MODE_PH, gmName));
                });
        //gamemode creative <player>
        ArgumentTree arg1 = new OfflinePlayerArgument()
                .executes((sender, args) -> {
                    handleTarget(sender, (OfflinePlayer) args[1], (GameMode) args[0]);
                });
        new Command(Commands.GAMEMODE_LABEL)
                .withPermission(Commands.GAMEMODE_PERMISSION)
                .withAliases(Commands.GAMEMODE_ALIASES)
                .then(arg0.then(arg1)).override();
    }

    /**
     * Handle target gamemode command.
     * @param sender Command sender
     * @param offTarget Offline player
     * @param gm Game mode
     */
    private void handleTarget(CommandSender sender, OfflinePlayer offTarget, GameMode gm) {
        Player target = offTarget.getPlayer();
        String gmName = getGmName(gm);
        if (target != null && target == sender) {
            target.setGameMode(gm);
            target.sendMessage(Lang.GAMEMODE_CHANGED.rp(IConfig.MODE_PH, gmName));
        } else {
            boolean gmChanged;
            //Change gamemodes
            if (target != null) {
                gmChanged = target.getGameMode() != gm;
                target.setGameMode(gm);
            } else {
                NBTPlayer nbtPlayer = new NBTPlayer(offTarget);
                int currentGamemode = nbtPlayer.getplayerGameType();
                int newGamemodeValue = gm.getValue();
                nbtPlayer.setPreviousPlayerGameType(currentGamemode);
                nbtPlayer.setplayerGameType(newGamemodeValue);
                gmChanged = (currentGamemode != newGamemodeValue);
                nbtPlayer.save();
            }
            if (gmChanged) {
                sender.sendMessage(Lang.GAMEMODE_TARGETS_GAMEMODE_CHANGED
                        .rp(IConfig.MODE_PH, gmName, IConfig.PLAYER_PH, offTarget.getName()));
            } else {
                sender.sendMessage(Lang.GAMEMODE_TARGET_ALREADY_IN_GAMEMODE
                        .rp(IConfig.MODE_PH, gmName, IConfig.PLAYER_PH, offTarget.getName()));
            }
        }
    }

    /**
     *
     * @param gm Gamemode
     * @return Gamemode translation from config
     */
    private String getGmName(GameMode gm) {
        return switch (gm) {
            case SURVIVAL -> Lang.GAMEMODE_SURVIVAL.getString();
            case CREATIVE -> Lang.GAMEMODE_CREATIVE.getString();
            case ADVENTURE -> Lang.GAMEMODE_ADVENTURE.getString();
            case SPECTATOR -> Lang.GAMEMODE_SPECTATOR.getString();
        };
    }

    /**
     *
     * @param input gamemode string
     * @return Gamemode based on aliases in config
     */
    private GameMode getGamemode(String input) {
        Predicate<Commands> hasGm = (gamemodes) -> Arrays
                .stream(gamemodes.getString().split(",")).anyMatch(input::equalsIgnoreCase);
        if (hasGm.test(Commands.GAMEMODE_ALIASES_SURVIVAL)) {
            return SURVIVAL;
        } else if (hasGm.test(Commands.GAMEMODE_ALIASES_CREATIVE)) {
            return GameMode.CREATIVE;
        } else if (hasGm.test(Commands.GAMEMODE_ALIASES_ADVENTURE)) {
            return GameMode.ADVENTURE;
        } else if (hasGm.test(Commands.GAMEMODE_ALIASES_SPECTATOR)) {
            return GameMode.SPECTATOR;
        } else {
            return null;
        }
    }
}
