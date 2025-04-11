package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.TimeArgument;
import io.github.zoltus.onecore.player.command.arguments.WorldsArgument;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import static io.github.zoltus.onecore.data.configuration.PlaceHolder.TIME_PH;
import static io.github.zoltus.onecore.data.configuration.PlaceHolder.WORLD_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.TIME_CHANGED;

public class Time implements ICommand {

    @Override
    public void init() {
        //TIME <TIME>
        Argument<?> arg0 = new TimeArgument(false)
                .executesPlayer((p, args) -> {
                    changeTime(p, (long) args.get(0), p.getWorld());
                });
        //TIME <TIME> <world>
        Argument<?> arg1 = new WorldsArgument()
                .executes((sender, args) -> {
                    changeTime(sender, (long) args.get(0), (World) args.get(1));
                });
        new Command(Commands.TIME_LABEL)
                .withPermission(Commands.TIME_PERMISSION)
                .withAliases(Commands.TIME_ALIASES)
                .then(arg0.then(arg1))
                .override();
    }

    private void changeTime(CommandSender sender, long time, World w) {
        w.setTime(time);
        TIME_CHANGED.rb(TIME_PH, w.getTime()).rb(WORLD_PH, w.getName()).send(sender);
    }
}
