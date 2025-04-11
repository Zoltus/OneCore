package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import java.time.Duration;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.PLAYTIME_TARGETS_PLAYTIME;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.PLAYTIME_YOUR_PLAYTIME;

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
                    String timeMessage = secondsToTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE));
                    PLAYTIME_YOUR_PLAYTIME
                            .rb(TIME_PH, timeMessage)
                            .send(player);
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

    private String secondsToTime(int ticks) {
        int totalSeconds = ticks / 20;
        Duration duration = Duration.ofSeconds(totalSeconds);
        long days = duration.toDays();
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        // Format DD:HH:MM:SS todo to config
        return String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
    }
}
