package io.github.zoltus.onecore.player.teleporting;


import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.player.User;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.github.zoltus.onecore.data.configuration.yamls.Config.SECONDS_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Config.TELEPORT_DELAY;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.TP_STARTED;

public final class Teleport {

    private static final OneCore plugin = OneCore.getPlugin();

    @Getter
    private static final Map<UUID, Teleport> teleports = new HashMap<>();
    private static final int DELAY = TELEPORT_DELAY.getInt();

    @Getter
    private final User teleporter, target;
    private final BukkitTask teleTask;
    private final Location loc;

    private Teleport(User teleporter, User target, Location loc) {
        this.loc = loc;
        this.target = target;
        this.teleporter = teleporter;
        this.teleTask = teleportTimer();
        teleports.put(teleporter.getUniqueId(), this);
    }

    public static void start(User teleporter, User target, Location loc) {
        UUID uuid = teleporter.getUniqueId();
        if (TeleportHandler.hasTeleport(uuid)) {
            TeleportHandler.getTeleport(uuid).cancel("");
        } else {
            new Teleport(teleporter, target, loc);
        }
    }

    private BukkitTask teleportTimer() {
        TP_STARTED.send(teleporter, SECONDS_PH, DELAY);
        return Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Location destination = target == null ? loc : target.getPlayer().getLocation();
            teleTask.cancel();
            teleports.remove(teleporter.getUniqueId());
            LocationUtils.teleportSafeAsync(teleporter.getPlayer(), destination);
        }, 20L * DELAY);
    }

    void cancel(String reason) {
        teleTask.cancel();
        teleports.remove(teleporter.getUniqueId());
        teleporter.sendMessage(reason);
    }
}
