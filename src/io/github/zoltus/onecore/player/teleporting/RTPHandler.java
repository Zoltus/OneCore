package io.github.zoltus.onecore.player.teleporting;

import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.utils.SpeedChangeScheduler;
import org.bukkit.Bukkit;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class RTPHandler {
    //Determines how often a player can teleport and find a location to teleport to.
    //todo config to set queuetimer when low tps
    private final OneCore plugin;
    //todo to settings
    private final int tickSpeed = 100;
    private static RTPHandler rtpHandler;
    private final SpeedChangeScheduler speedChangeScheduler;
    //Queue for teleports
    private final ConcurrentLinkedQueue<UUID> playerQueue = new ConcurrentLinkedQueue<>();
    //Map for cooldowns
    //todo
    private final ConcurrentHashMap<UUID, Long> lastTeleportTime = new ConcurrentHashMap<>();

    //todo calls safeloc multiple times.
    private RTPHandler(OneCore plugin) {
        this.plugin = plugin;
        //loops through the queue and teleports players
        //slowing scheduler so it can be slowed when wanted
        this.speedChangeScheduler = new SpeedChangeScheduler(plugin, tickSpeed, false, () -> {
            if (!playerQueue.isEmpty() && playerQueue.peek() != null) {
                UUID uuid = playerQueue.poll();
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    unQueue(uuid);
                    Bukkit.broadcastMessage("Player is null and was removed from que");
                } else {
                    User user = User.of(player);
                    CompletableFuture<Location> loc = getRandomLocAsync(player);
                    loc.thenAccept(location -> {
                        user.sendMessage("teleporttimer");
                        user.teleportTimer(location);
                        //user.sendMessage(RTP_TELEPORTED.getString());
                        unQueue(uuid);
                    });
                }
            }
        });
    }

    public void changeQueueTimer(int timer) {
        speedChangeScheduler.reSchedule(timer);
    }

    private CompletableFuture<Location> getRandomLocAsync(Player p) {
        return CompletableFuture.supplyAsync(() -> {
            Block b;
            Location loc;
            Supplier<Integer> randomInt = () -> ThreadLocalRandom.current().nextInt(-100, 100);
            do {
                try {
                    plugin.getLogger().info("ScanLoc");
                    b = p.getWorld().getHighestBlockAt(randomInt.get(), randomInt.get(), HeightMap.MOTION_BLOCKING);
                    loc = LocationUtils.getSafeLocation(p, b.getLocation());
                    //Scans 1 loc per second //todo to config
                    int ticks = 20; //todo to config
                    wait(50 * ticks);
                } catch (InterruptedException e) {
                   e.printStackTrace();
                   return null;
                }
            } while (loc == null);
            return b.getLocation().add(0.5, 1, 0.5);
        });
    }

    public static RTPHandler init(OneCore oneCore) {
        return rtpHandler = rtpHandler == null ? new RTPHandler(oneCore) : rtpHandler;
    }

    public boolean hasCooldown(UUID uuid) {
        return System.currentTimeMillis() < lastTeleportTime.getOrDefault(uuid, System.currentTimeMillis());
    }

    public boolean insideWorldBorder(Location loc) {
        return loc.getWorld() != null && loc.getWorld().getWorldBorder().isInside(loc);
    }

    public void queue(UUID uuid) {
        playerQueue.add(uuid);
    }

    //Cancel teleport
    public void unQueue(UUID uuid) {
        playerQueue.remove(uuid);
        //Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(p -> p.sendMessage("Teleport cancelled"));
    }

    public boolean isQueued(UUID uuid) {
        return playerQueue.contains(uuid);
    }

    public boolean getEstimatedQueueTime(UUID uuid) {
        return false;
    }


}
