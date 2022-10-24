package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.ArgumentTree;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import io.github.zoltus.onecore.player.command.Command;

import java.text.SimpleDateFormat;
import java.util.Date;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class PlayTime implements ICommand {

    @Override
    public void init() {
        //playtime <player>
        ArgumentTree arg0 = new OfflinePlayerArgument()
                .executes((sender, args) -> {
                    OfflinePlayer offTarget = (OfflinePlayer) args[0];
                    int playtime = offTarget.getStatistic(Statistic.PLAY_ONE_MINUTE);
                    String timeMessage = secondsToTime(playtime);
                    String message = PLAYTIME_TARGETS_PLAYTIME.rp(TIME_PH, timeMessage, PLAYER_PH, offTarget.getName());
                    sender.sendMessage(message);
                });
        //playtime
        new Command(PLAYTIME_LABEL)
                .withPermission(PLAYTIME_PERMISSION)
                .withAliases(PLAYTIME_ALIASES)
                .executesPlayer((player, args) -> {
                    String timeMessage = secondsToTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE));
                    String message = PLAYTIME_YOUR_PLAYTIME.rp(TIME_PH, timeMessage);
                    player.sendMessage(message);
                }).then(arg0)
                .override();
    }

    private String secondsToTime(int seconds) {
        SimpleDateFormat df = new SimpleDateFormat(PLAYTIME_TIME_FORMAT.getString());
        return df.format(new Date((seconds / 20) * 1000L));
    }
}
