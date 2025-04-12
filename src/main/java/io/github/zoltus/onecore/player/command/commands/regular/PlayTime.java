package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import java.util.concurrent.TimeUnit;

import static io.github.zoltus.onecore.data.configuration.PlaceHolder.PLAYER_PH;
import static io.github.zoltus.onecore.data.configuration.PlaceHolder.TIME_PH;
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
                    String format = PLAYTIME_TIME_FORMAT.get();
                    int playTimeTicks = offTarget.getStatistic(Statistic.PLAY_ONE_MINUTE);
                    String timeMessage = secondsToTime(playTimeTicks, format);
                    PLAYTIME_TARGETS_PLAYTIME
                            .rb(TIME_PH, timeMessage)
                            .rb(PLAYER_PH, offTarget.getName())
                            .send(sender);
                });
        //playtime
        new Command(PLAYTIME_LABEL)
                .withPermission(PLAYTIME_PERMISSION)
                .withAliases(PLAYTIME_ALIASES)
                .executesPlayer((player, args) -> {
                    String format = PLAYTIME_TIME_FORMAT.get();
                    int playTimeTicks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
                    String timeMessage = secondsToTime(playTimeTicks, format);
                    PLAYTIME_YOUR_PLAYTIME
                            .rb(TIME_PH, timeMessage)
                            .send(player);
                }).then(arg0)
                .override();
    }
    public static String secondsToTime(int ticks, String format) {
        long totalSeconds = (long) ticks / 20;
        long totalMillis = totalSeconds * 1000;
        if (format == null || format.isEmpty()) {
            format = "dd:HH:mm:ss"; // Default format
        }

        try {
            return DurationFormatUtils.formatDuration(totalMillis, format, true);
        } catch (Exception e) {
            System.err.println("Error formatting duration with format '" + format + "': " + e.getMessage());
            long days = TimeUnit.MILLISECONDS.toDays(totalMillis);
            long hours = TimeUnit.MILLISECONDS.toHours(totalMillis) % 24;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(totalMillis) % 60;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(totalMillis) % 60;
            return String.format("%02d:%02d:%02d:%02d (fallback)", days, hours, minutes, seconds);
        }
    }
}


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