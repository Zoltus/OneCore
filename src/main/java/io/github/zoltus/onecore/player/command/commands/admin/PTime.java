package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.PlayerArgument;
import io.github.zoltus.onecore.player.command.arguments.TimeArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class PTime implements ICommand {

    @Override
    public void init() {
        //TIME <time>
        Argument<?> arg0 = new TimeArgument()
                .executesPlayer((p, args) -> {
                    changeTime(p, (long) args.get(0), p);
                });
        //TIME <time> <player>
        Argument<?> arg1 = new PlayerArgument()
                .executes((sender, args) -> {
                    changeTime(sender, (long) args.get(0), (Player) args.get(1));
                });
        new Command(Commands.PTIME_LABEL)
                .withPermission(Commands.PTIME_PERMISSION)
                .withAliases(Commands.PTIME_ALIASES)
                .then(arg0.then(arg1))
                .override();
    }

    private void changeTime(CommandSender sender, long time, Player target) {
        target.setPlayerTime(time, false);
        if (sender != target) {
            PTIME_CHANGED_OTHER.send(sender, TIME_PH, target.getPlayerTime(), PLAYER_PH, target.getName());
        }
        PTIME_CHANGED.send(sender, TIME_PH, target.getPlayerTime(), PLAYER_PH, target.getName());
    }
}
