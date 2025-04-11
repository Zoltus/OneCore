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
        Argument<?> arg0 = new TimeArgument(true)
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
    //todo reset

    private void changeTime(CommandSender sender, long time, Player target) {
        if (time < 0) {
            target.resetPlayerTime();
        } else {
            target.setPlayerTime(time, false);
            PTIME_CHANGED.rb(TIME_PH, target.getPlayerTime())
                    .rb(PLAYER_PH, target.getName())
                    .send(sender);
            if (sender != target) {
                PTIME_CHANGED_OTHER.rb(TIME_PH, target.getPlayerTime())
                        .rb(PLAYER_PH, target.getName())
                        .send(sender);
            }
        }
    }
}
