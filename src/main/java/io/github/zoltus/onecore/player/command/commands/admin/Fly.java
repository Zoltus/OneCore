package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import io.github.zoltus.onecore.player.nbt.NBTPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.zoltus.onecore.data.configuration.PlaceHolder.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.FLY_YOUR_FLIGHT_IS_NOW;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.FLY_YOU_SWITCHED_TARGET;

public class Fly implements ICommand {

    @Override
    public void init() {
        //fly <player>
        Argument<?> arg0 = new OfflinePlayerArgument()
                .withPermission(Commands.FLY_PERMISSION_OTHER.asPermission())
                .executes((sender, args) -> {
                    handle(sender, (OfflinePlayer) args.get(0), null);
                });
        //fly <player> true/false
        Argument<?> arg1 = new BooleanArgument(Lang.NODES_TRUE_FALSE.get())
                .withPermission(Commands.FLY_PERMISSION_OTHER.asPermission())
                .executes((sender, args) -> {
                    handle(sender, (OfflinePlayer) args.get(0), (Boolean) args.get(1));
                });
        new Command(Commands.FLY_LABEL)
                .withPermission(Commands.FLY_PERMISSION)
                .withAliases(Commands.FLY_ALIASES)
                .executesPlayer((PlayerCommandExecutor) (sender, args) -> handle(sender, sender, null))
                .then(arg0.then(arg1)).override();
    }

    private void handle(CommandSender sender, OfflinePlayer offP, Boolean fly) {
        boolean result = offP.getPlayer() != null ? setOnlineFly(offP.getPlayer(), fly) : setOfflineFly(offP, fly);
        if (sender != offP.getPlayer()) {
            FLY_YOU_SWITCHED_TARGET
                    .rb(PLAYER_PH, offP.getName())
                    .rb(TOGGLE_PH, !result)
                    .send(sender);
        }
    }

    private boolean setOnlineFly(Player onlTarget, Boolean fly) {
        boolean flyResult = fly == null ? onlTarget.getAllowFlight() : fly;
        onlTarget.setAllowFlight(!flyResult);
        onlTarget.setFlying(!flyResult);
        FLY_YOUR_FLIGHT_IS_NOW.rb(TOGGLE_PH, !flyResult).send(onlTarget);
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