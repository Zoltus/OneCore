package io.github.zoltus.onecore.player.teleporting;

import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import static io.github.zoltus.onecore.data.configuration.PlaceHolder.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Config.TELEPORT_DELAY;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.TP_STARTED;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.TP_TARGET_QUIT;

public class DelayedTeleport {

    private static final OneCore plugin = OneCore.getPlugin();
    private static final int DELAY = TELEPORT_DELAY.getInt();

    private Location loc;
    private final User user;
    //Target to teleport to, if null it will teleport to loc
    private User target;
    private final BukkitTask teleTask;

    public DelayedTeleport(User user, Location loc) {
        this.loc = loc;
        this.user = user;
        this.teleTask = start();
    }

    public DelayedTeleport(User user, User target) {
        this.target = target;
        this.user = user;
        this.teleTask = start();
    }

    //Todo offline support, mayby on quitevent finish teleport, and cancel task?
    private BukkitTask start() {
        TP_STARTED.rb(SECONDS_PH, DELAY).send(user);
        return Bukkit.getScheduler().runTaskLater(plugin, () -> {
            //If target is set and isnt online
            if (target != null && !target.isOnline()) {
                TP_TARGET_QUIT.rb(PLAYER_PH, target.getName()).send(user); // todo test
            } else {
                Location destination = target == null ? loc : target.getPlayer().getLocation();
                //Checks if teleporter is still online.
                if (user.isOnline()) {
                    LocationUtils.teleportWMountSafeAsync(user.getPlayer(), destination);
                }
            }
            user.setTeleport(null);
        }, 20L * DELAY);
    }

    public void cancel(Lang reason) {
        teleTask.cancel();
        if (reason != null) {
            reason.send(user);
            //Sets teleport to null so that the user can teleport again
            user.setTeleport(null);
        }
    }
}
