package sh.zoltus.onecore.player.command.commands.regular;

import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;

import java.text.SimpleDateFormat;
import java.util.Date;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.PLAYER_PH;
import static sh.zoltus.onecore.data.configuration.yamls.Commands.TIME_PH;
import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.*;

public class PlayTime implements IOneCommand {

    @Override
    public void init() {
        //playtime
        command(PLAYTIME_LABEL)
                .withPermission(PLAYTIME_PERMISSION)
                .withAliases(PLAYTIME_ALIASES)
                .executesPlayer((player, args) -> {
                    String timeMessage = secondsToTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE));
                    String message = PLAYTIME_YOUR_PLAYTIME.rp(TIME_PH, timeMessage);
                    player.sendMessage(message);
                }).override();
        //playtime <player>
        command(PLAYTIME_LABEL)
                .withPermission(PLAYTIME_OTHER_PERMISSION)
                .withAliases(PLAYTIME_ALIASES)
                .withArguments(new OfflinePlayerArgument())
                .executes((sender, args) -> {
                    OfflinePlayer offTarget = (OfflinePlayer) args[0];
                    int playtime = offTarget.getStatistic(Statistic.PLAY_ONE_MINUTE);
                    String timeMessage = secondsToTime(playtime);
                    String message = PLAYTIME_TARGETS_PLAYTIME.rp(TIME_PH, timeMessage, PLAYER_PH, offTarget.getName());
                    sender.sendMessage(message);
                }).register();
    }

    private String secondsToTime(int seconds) {
        SimpleDateFormat df = new SimpleDateFormat(PLAYTIME_TIME_FORMAT.getString());
        return df.format(new Date((seconds / 20) * 1000L));
    }
}
