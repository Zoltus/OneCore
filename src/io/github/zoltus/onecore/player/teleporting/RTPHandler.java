package io.github.zoltus.onecore.player.teleporting;

import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.utils.SpeedChangeScheduler;
import org.bukkit.Bukkit;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import javax.inject.Singleton;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.RTP_TELEPORTED;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.TP_NO_SAFE_LOCATIONS;

@Singleton
public class RTPHandler {
    //todo cooldowns for rtp for non-vip players
    //Determines how often a player can teleport and find a location to teleport to.
    //todo config to set queuetimer when low tps
    private final OneCore plugin;
    //todo to settings
    private static RTPHandler rtpHandler;
    private final SpeedChangeScheduler speedChangeScheduler;
    //Queue for teleports todo heck if unique
    private final ConcurrentLinkedQueue<UUID> playerQueue = new ConcurrentLinkedQueue<>();
    //Map for cooldowns
    private boolean busy = false;
    //Scan 1 location every 1000 ms  //todo to config for both
    private final int RTP_QUEUE_TICK = Config.RTP_QUEUE_TICK.getInt();
    private final long DELAY_BETWEEN_LOC_SCANS = Config.RTP_DELAY_BETWEEN_LOCATION_SCAN.getLong();
    private final int SCANS_TILL_TIME_OUT = Config.RTP_SCANS_TILL_TIMEOUT.getInt();
    private final int RTP_RADIUS = Config.RTP_RADIUS.getInt();

    private RTPHandler(OneCore plugin) {
        this.plugin = plugin;
        this.speedChangeScheduler = startRTPScheduler();
    }

    //loops through the queue and teleports players ever x amount of times
    private SpeedChangeScheduler startRTPScheduler() {
        return new SpeedChangeScheduler(plugin, RTP_QUEUE_TICK, true, () -> {
            //If tp is inprogress, skip
            if (busy) return;
            if (!playerQueue.isEmpty()) {
                UUID uuid = playerQueue.poll();
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    unQueue(uuid);
                } else {
                    User user = User.of(player);
                    busy = true;
                    //Finds random loc async and then teleports player to it
                    CompletableFuture<Location> randomLoc = getRandomLocAsync(player);
                    randomLoc.thenAccept(location -> {
                        if (location == null) { //todo safeloc msg in 2places cleanuo
                            user.sendMessage(TP_NO_SAFE_LOCATIONS.getString());
                            unQueue(uuid);
                        } else {
                            //return to main thread for tele and asyncchunk stuff to work
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                user.teleportTimer(location);
                                user.sendMessage(RTP_TELEPORTED.getString());
                                unQueue(uuid);
                            });
                        }
                    });
                }
            }
        });
    }

    private CompletableFuture<Location> getRandomLocAsync(Player p) {
        return CompletableFuture.supplyAsync(() -> {
            int scanCount = 0;
            long lastScanMs = 0;
            Block b;
            Location loc = null;
            Supplier<Integer> randomInt = () -> ThreadLocalRandom.current().nextInt(-RTP_RADIUS, RTP_RADIUS + 1);
            do {
                //Check if scandelayms has passed
                if (System.currentTimeMillis() - lastScanMs > DELAY_BETWEEN_LOC_SCANS) {
                    b = p.getWorld().getHighestBlockAt(randomInt.get(), randomInt.get(), HeightMap.MOTION_BLOCKING);
                    loc = LocationUtils.getSafeLocation(p, b.getLocation());
                    //Scans 1 loc per second
                    lastScanMs = System.currentTimeMillis();
                    //Checks if scanlimit is reached, if not scans again
                    scanCount++;
                }
            } while (loc == null && scanCount < SCANS_TILL_TIME_OUT || (loc != null && !insideWorldBorder(loc)));
            //If scan timeouts it returns null location, adds 1 because block y
            return loc == null ? null : loc.add(0.5, 1.1, 0.5);
        });
    }

    //todo change this, if radius is 10000 and worldborder is 10 it will never find safe loc
    public boolean insideWorldBorder(Location loc) {
       // loc.getWorld().getWorldBorder().getMaxCenterCoordinate()
        return loc.getWorld() != null && loc.getWorld().getWorldBorder().isInside(loc);
    }

    public void changeQueueTimer(int timer) {
        speedChangeScheduler.reSchedule(timer);
    }

    public static RTPHandler init(OneCore oneCore) {
        return rtpHandler = rtpHandler == null ? new RTPHandler(oneCore) : rtpHandler;
    }

    public void queue(UUID uuid) {
        Bukkit.broadcastMessage("added to queue");
        playerQueue.add(uuid);
    }

    //Cancel teleport
    public void unQueue(UUID uuid) {
        playerQueue.remove(uuid);
        busy = false;
    }
}
