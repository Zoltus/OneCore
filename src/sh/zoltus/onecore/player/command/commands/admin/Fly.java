package sh.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.Command;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import sh.zoltus.onecore.player.nbt.NBTPlayer;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.*;

public class Fly implements IOneCommand {

    //todo change to using oneuser only when needed
    @Override
    public void init() {
        //fly <player>
        ArgumentTree arg0 = new OfflinePlayerArgument()
                .executes((sender, args) -> {
                    handle(sender, (OfflinePlayer) args[0], null);
                });
        //fly <player> true/false
        ArgumentTree arg1 = new BooleanArgument(NODES_TRUE_FALSE.getString())
                .executes((sender, args) -> {
                    handle(sender, (OfflinePlayer) args[0], (Boolean) args[1]);
                });
        new Command(FLY_LABEL)
                .withPermission(FLY_PERMISSION)
                .withAliases(FLY_ALIASES)
                .executesPlayer((PlayerCommandExecutor) (sender, args) -> handle(sender, sender, null))
                .then(arg0.then(arg1)).override();
    }

    private void handle(CommandSender sender, OfflinePlayer offP, Boolean fly) {
        boolean result = offP.getPlayer() != null ? setOnlineFly(offP.getPlayer(), fly) : setOfflineFly(offP, fly);
        if (sender != offP.getPlayer()) {
            sender.sendMessage(FLY_YOU_SWITCHED_TARGET.rp(PLAYER_PH, offP.getName(), TOGGLE_PH, result));
        }
    }

    private boolean setOnlineFly(Player onlTarget, Boolean fly) {
        boolean flyResult = fly == null ? onlTarget.getAllowFlight() : fly;
        onlTarget.setAllowFlight(!flyResult);
        onlTarget.setFlying(!flyResult);
        onlTarget.sendMessage(FLY_YOUR_FLIGHT_IS_NOW.rp(TOGGLE_PH, flyResult));
        return flyResult;
    }

    private boolean setOfflineFly(OfflinePlayer offP, Boolean fly) {
        NBTPlayer nbtPlayer = new NBTPlayer(offP);
        boolean flyResult = fly == null ? nbtPlayer.getMayfly() : fly;
        nbtPlayer.setMayfly(!flyResult);
        nbtPlayer.setFlying(!flyResult);
        nbtPlayer.save();
        return flyResult;
    }
}