package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import java.text.SimpleDateFormat;
import java.util.Date;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.PLAYER_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Commands.TIME_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class PlayTime implements ICommand {

    @Override
    public void init() {
        //playtime <player>
        Argument<?> arg0 = new OfflinePlayerArgument()
                .withPermission(Commands.PLAYTIME_PERMISSION_OTHER.asPermission())
                .executes((sender, args) -> {
                    OfflinePlayer offTarget = (OfflinePlayer) args.get(0);
                    int playtime = offTarget.getStatistic(Statistic.PLAY_ONE_MINUTE);
                    String timeMessage = secondsToTime(playtime);
                    PLAYTIME_TARGETS_PLAYTIME.send(sender, TIME_PH, timeMessage, PLAYER_PH, offTarget.getName());
                });
        //playtime
        new Command(PLAYTIME_LABEL)
                .withPermission(PLAYTIME_PERMISSION)
                .withAliases(PLAYTIME_ALIASES)
                .executesPlayer((player, args) -> {
                    String timeMessage = secondsToTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE));
                    PLAYTIME_YOUR_PLAYTIME.send(player, TIME_PH, timeMessage);
                }).then(arg0)
                .override();

        /* todo? Optional
        new CommandAPICommand("asd")
                .withPermission(PLAYTIME_PERMISSION.getString())
                .withAliases(PLAYTIME_ALIASES.getString())
                .withOptionalArguments(new OfflinePlayerArgument())
                .executesPlayer((sender, args) -> {
                    OfflinePlayer offTarget = (OfflinePlayer) args.getOptional(0).orElse(null);
                    if (offTarget == null) {
                        String timeMessage = secondsToTime(sender.getStatistic(Statistic.PLAY_ONE_MINUTE));
                        PLAYTIME_YOUR_PLAYTIME.send(sender, TIME_PH, timeMessage);
                    } else {
                        int playtime = offTarget.getStatistic(Statistic.PLAY_ONE_MINUTE);
                        String timeMessage = secondsToTime(playtime);
                        PLAYTIME_TARGETS_PLAYTIME.send(sender, TIME_PH, timeMessage, PLAYER_PH, offTarget.getName());
                    }
                }).override();*/
    }

    private String secondsToTime(int seconds) {
        SimpleDateFormat df = new SimpleDateFormat(PLAYTIME_TIME_FORMAT.getString());
        return df.format(new Date((seconds / 20) * 1000L));
    }
}
