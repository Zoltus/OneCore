package sh.zoltus.onecore.player.command.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.ServerStatisticManager;
import net.minecraft.stats.StatisticList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.*;

public class PlayTime implements IOneCommand {

    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                //playtime
                command(PLAYTIME_LABEL)
                        .withPermission(PLAYTIME_PERMISSION)
                        .withAliases(PLAYTIME_ALIASES)
                        .executesPlayer((player, args) -> {
                    String timeMessage = secondsToTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE));
                    String message = PLAYTIME_YOUR_PLAYTIME.rp(TIME_PH, timeMessage);
                    player.sendMessage(message);
                }),
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
                })
        };
    }

    private String secondsToTime(int seconds) {
        SimpleDateFormat df = new SimpleDateFormat(PLAYTIME_TIME_FORMAT.getString());
        return df.format(new Date((seconds / 20) * 1000L));
    }

    private int getOfflineTime(OfflinePlayer offPlayer) {
        World w = Bukkit.getWorlds().get(0);
        MinecraftServer mcServer = ((CraftWorld) w).getHandle().n();
        File jsonFile = new File(w.getWorldFolder().getAbsolutePath() + "/stats/" + offPlayer.getUniqueId().toString().toLowerCase() + ".json");
        ServerStatisticManager statManager = new ServerStatisticManager(mcServer, jsonFile);
        return statManager.a(StatisticList.i.b(StatisticList.k));

    }
}
