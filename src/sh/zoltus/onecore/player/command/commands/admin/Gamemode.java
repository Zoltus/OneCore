package sh.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.configuration.yamls.Commands;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import sh.zoltus.onecore.player.nbt.NBTPlayer;

import java.util.Arrays;
import java.util.function.Predicate;

import static org.bukkit.GameMode.SURVIVAL;
import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Config.PERMISSION_PREFIX;
import static sh.zoltus.onecore.configuration.yamls.Lang.*;

public class Gamemode implements IOneCommand {

    private Argument<?> gamemodeArgument() {
        return new CustomArgument<>(new StringArgument(NODES_GAMEMODE.getString()), (info) -> {
            GameMode gm = getGamemode(info.input());
            if (gm == null) {
                throw new CustomArgument.CustomArgumentException(GAMEMODE_INVALID_GAMEMODE.getString());
            } else if (!info.sender().hasPermission(GAMEMODE_MODE_PERMISSION.getAsPermission() + gm.name().toLowerCase())) {
                throw new CustomArgument.CustomArgumentException(GAMEMODE_MODE_PERMISSION_MISSING.rp(MODE_PH, PERMISSION_PREFIX.getAsPermission() + gm.name().toLowerCase()));
            } else {
                return gm;
            }
        }).replaceSuggestions(ArgumentSuggestions
                .strings((info) -> toSuggestion(info.currentArg(), GAMEMODE_SUGGESTIONS.getSplitArr())));
    }

    @Override
    public void init() {
        //gamemode creative
        command(GAMEMODE_LABEL)
                .withPermission(GAMEMODE_PERMISSION)
                .withAliases(GAMEMODE_ALIASES)
                .withArguments(gamemodeArgument())
                .executesPlayer((player, args) -> {
                    GameMode gm = (GameMode) args[0];
                    String gmName = getGmName(gm);
                    player.setGameMode(gm);
                    player.sendMessage(GAMEMODE_CHANGED.rp(MODE_PH, gmName));
                }).override();
        //gamemode creative <player>
        command(GAMEMODE_LABEL)
                .withPermission(GAMEMODE_OTHER_PERMISSION)
                .withAliases(GAMEMODE_ALIASES)
                .withArguments(gamemodeArgument(), new OfflinePlayerArgument())
                .executes((sender, args) -> handleTarget(sender, (OfflinePlayer) args[1], (GameMode) args[0]))
                .register();

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
            target.sendMessage(GAMEMODE_CHANGED.rp(MODE_PH, gmName));
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
                sender.sendMessage(GAMEMODE_TARGETS_GAMEMODE_CHANGED
                        .rp(MODE_PH, gmName, PLAYER_PH, offTarget.getName()));
            } else {
                sender.sendMessage(GAMEMODE_TARGET_ALREADY_IN_GAMEMODE
                        .rp(MODE_PH, gmName, PLAYER_PH, offTarget.getName()));
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
            case SURVIVAL -> GAMEMODE_SURVIVAL.getString();
            case CREATIVE -> GAMEMODE_CREATIVE.getString();
            case ADVENTURE -> GAMEMODE_ADVENTURE.getString();
            case SPECTATOR -> GAMEMODE_SPECTATOR.getString();
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
        if (hasGm.test(GAMEMODE_ALIASES_SURVIVAL)) {
            return SURVIVAL;
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
