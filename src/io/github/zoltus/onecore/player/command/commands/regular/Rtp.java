package io.github.zoltus.onecore.player.command.commands.regular;

import io.github.zoltus.onecore.player.command.ICommand;
import org.bukkit.Bukkit;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.teleporting.LocationUtils;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Config.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class Rtp implements ICommand {

    private static final int RADIUS = Config.RTP_RADIUS.getInt();
    private static final HashMap<UUID, Long> timers = new HashMap<>();
    private static final ArrayDeque<UUID> queue = new ArrayDeque<>(); //todo

    //todo /rtp <player>, todo rtp worker, max rtps per second to conig
    //every player in queue = teleport timer +1s
    @Override
    public void init() {
        new Command(RTP_LABEL)
                .withAliases(RTP_ALIASES)
                .withPermission(RTP_PERMISSION)
                .executesPlayer((p, args) -> {
                    UUID uuid = p.getUniqueId();
                    Long rtpTime = timers.getOrDefault(uuid, System.currentTimeMillis());
                    long cooldownSeconds = (RTP_COOLDOWN_SECONDS.getInt() * 1000L);
                    //Checks if time has passed
                    if (!hasCooldown(rtpTime)) {
                        handleRtp(p, uuid, cooldownSeconds);
                    } else {
                        long secondsLeft = (rtpTime + cooldownSeconds - System.currentTimeMillis()) / 1000;
                        RTP_ON_COOLDOWN.send(p, SECONDS_PH, secondsLeft);
                    }
                }).override();
    }

    //todo add to teleporter list and use premade teleport
    private void handleRtp(Player p, UUID uuid, long cooldownSeconds) {
        timers.put(uuid, System.currentTimeMillis() + cooldownSeconds);
        queue.add(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Location loc = getRandomLocation(p);
            Bukkit.getScheduler().runTask(plugin, () -> { //todo asynctele
                //todo canBreak(p, loc)
                p.teleport(loc);
                p.sendMessage(RTP_TELEPORTED.getString());
                p.sendMessage("22");
                queue.removeLast();
            });
        });
    }

    private boolean hasCooldown(long rtpTime) {
        return System.currentTimeMillis() < rtpTime;
    }

    //todo canbreak
    /**
     * Gets random location
     *
     * @param p player
     * @return Location
     */
    private Location getRandomLocation(Player p) { //todo scan locations chunkscan per second limit
        Block b;
        Location loc;
        Supplier<Integer> randomInt = () -> ThreadLocalRandom.current().nextInt(-RADIUS, RADIUS);
        do {
            Bukkit.getConsoleSender().sendMessage("ScanLoc");
            b = p.getWorld().getHighestBlockAt(randomInt.get(), randomInt.get(), HeightMap.MOTION_BLOCKING);
            loc = LocationUtils.getSafeLocation(p, b.getLocation());
        } while (loc == null);
        return b.getLocation().add(0.5, 1, 0.5);
    }
}
