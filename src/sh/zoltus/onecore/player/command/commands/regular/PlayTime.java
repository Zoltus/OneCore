package sh.zoltus.onecore.player.command.commands.regular;

import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import sh.zoltus.onecore.player.nbt.NBTPlayer;

import java.text.SimpleDateFormat;
import java.util.Date;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.*;

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
                }).register();
        //playtime <player>
        command(PLAYTIME_LABEL)
                .withPermission(PLAYTIME_OTHER_PERMISSION)
                .withAliases(PLAYTIME_ALIASES)
                .withArguments(new OfflinePlayerArgument())
                .executes((sender, args) -> {
                    OfflinePlayer offTarget = (OfflinePlayer) args[0];
                    Player onlTarget = offTarget.getPlayer();
                    int playtime = onlTarget != null ? onlTarget.getStatistic(Statistic.PLAY_ONE_MINUTE) : getOfflineTime(offTarget);
                    String timeMessage = secondsToTime(playtime);
                    String message = PLAYTIME_TARGETS_PLAYTIME.rp(TIME_PH, timeMessage, PLAYER_PH, offTarget.getName());
                    sender.sendMessage(message);
                }).register();

    }

    private String secondsToTime(int seconds) {
        SimpleDateFormat df = new SimpleDateFormat(PLAYTIME_TIME_FORMAT.getString());
        return df.format(new Date((seconds / 20) * 1000L));
    }

    private int getOfflineTime(OfflinePlayer offPlayer) {
        return new NBTPlayer(offPlayer).getStats().getData().getCustom().getPlayTime();
    }


}
