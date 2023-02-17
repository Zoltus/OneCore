package io.github.zoltus.onecore.player.teleporting;

import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.player.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import static io.github.zoltus.onecore.data.configuration.IConfig.PLAYER_PH;
import static io.github.zoltus.onecore.data.configuration.IConfig.SECONDS_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Config.TELEPORT_DELAY;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.TP_STARTED;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.TP_TARGET_QUIT;

public class Teleport {

    private static final OneCore plugin = OneCore.getPlugin();
    private static final int DELAY = TELEPORT_DELAY.getInt();

    private Location loc;
    private final User user;
    private User target;
    private final BukkitTask teleTask;

    public Teleport(User user, Location loc) {
        this.loc = loc;
        this.user = user;
        this.teleTask = start();
    }

    public Teleport(User user, User target) {
        this.target = target;
        this.user = user;
        this.teleTask = start();
    }

    private BukkitTask start() {
        TP_STARTED.send(user, SECONDS_PH, DELAY);
        return Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (target != null && !target.isOnline()) {
                TP_TARGET_QUIT.replace(PLAYER_PH, target.getName());
            } else {
                Location destination = target == null ? loc : target.getPlayer().getLocation();
                LocationUtils.teleportSafeAsync(user.getPlayer(), destination);
            }
            user.setTeleport(null);
        }, 20L * DELAY);
    }

    public void cancel(String reason) {
        teleTask.cancel();
        if (reason != null) {
            user.sendMessage(reason);
            //Sets teleport to null so that the user can teleport again
            user.setTeleport(null);
        }
    }
}
