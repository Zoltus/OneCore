package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.GamemodeArgument;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import io.github.zoltus.onecore.player.nbt.NBTPlayer;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class Gamemode implements ICommand {

    @Override
    public void init() {
        //gamemode creative
        Argument<?> arg0 = new GamemodeArgument()
                .executesPlayer((player, args) -> {
                    GameMode gm = (GameMode) args.get(0);
                    String gmName = getGmName(gm);
                    player.setGameMode(gm);
                    GAMEMODE_CHANGED.send(player, MODE_PH, gmName);
                });
        //gamemode creative <player>
        Argument<?> arg1 = new OfflinePlayerArgument()
                .executes((sender, args) -> {
                    handleTarget(sender, (OfflinePlayer) args.get(1), (GameMode) args.get(0));
                });
        //gamemode
        new Command(GAMEMODE_LABEL)
                .withPermission(GAMEMODE_PERMISSION)
                .withAliases(GAMEMODE_ALIASES)
                .then(arg0.then(arg1))
                .override();
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
            GAMEMODE_CHANGED.send(sender, MODE_PH, gmName);
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
                GAMEMODE_TARGETS_GAMEMODE_CHANGED.send(sender, MODE_PH, gmName, PLAYER_PH, sender.getName());
            } else {
                GAMEMODE_TARGET_ALREADY_IN_GAMEMODE.send(sender, MODE_PH, gmName, PLAYER_PH, sender.getName());
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
}
