package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.ArgumentTree;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.IOneCommand;
import io.github.zoltus.onecore.player.command.arguments.GamemodeArgument;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import io.github.zoltus.onecore.player.nbt.NBTPlayer;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;

public class Gamemode implements IOneCommand {

    @Override
    public void init() {
        //gamemode creative
        ArgumentTree arg0 = new GamemodeArgument()
                .executesPlayer((player, args) -> {
                    GameMode gm = (GameMode) args[0];
                    String gmName = getGmName(gm);
                    player.setGameMode(gm);
                    player.sendMessage(Lang.GAMEMODE_CHANGED.rp(MODE_PH, gmName));
                });
        //gamemode creative <player>
        ArgumentTree arg1 = new OfflinePlayerArgument()
                .executes((sender, args) -> {
                    handleTarget(sender, (OfflinePlayer) args[1], (GameMode) args[0]);
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
            target.sendMessage(Lang.GAMEMODE_CHANGED.rp(MODE_PH, gmName));
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
                        .rp(MODE_PH, gmName, PLAYER_PH, offTarget.getName()));
            } else {
                sender.sendMessage(Lang.GAMEMODE_TARGET_ALREADY_IN_GAMEMODE
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
            case SURVIVAL -> Lang.GAMEMODE_SURVIVAL.getString();
            case CREATIVE -> Lang.GAMEMODE_CREATIVE.getString();
            case ADVENTURE -> Lang.GAMEMODE_ADVENTURE.getString();
            case SPECTATOR -> Lang.GAMEMODE_SPECTATOR.getString();
        };
    }
}
