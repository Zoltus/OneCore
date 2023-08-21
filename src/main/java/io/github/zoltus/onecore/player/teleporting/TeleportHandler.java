package io.github.zoltus.onecore.player.teleporting;

import io.github.zoltus.onecore.player.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class TeleportHandler implements Listener {
    // Cancels Teleport on Move
    @EventHandler(priority = EventPriority.MONITOR)
    public static void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.hasMetadata("NPC")) return;
        Location fromLoc = e.getFrom();
        Location toLoc = e.getTo();
        User user = User.of(p);
        if (toLoc != null
                && user.getTeleport() != null
                && !fromLoc.getBlock().equals(toLoc.getBlock())) {
            DelayedTeleport teleport = user.getTeleport();
            if (teleport != null) {
                user.getTeleport().cancel(TP_CANCELLED_BY_MOVEMENT.getString());
            }
        }
    }

    // Cancels if damaged
    @EventHandler(priority = EventPriority.MONITOR)
    public static void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (p.hasMetadata("NPC") || e.isCancelled())
                return;
            User user = User.of(p);
            DelayedTeleport teleport = user.getTeleport();
            if (teleport != null) {
                user.getTeleport().cancel(TP_CANCELLED_BY_DAMAGE.getString());
            }
        }
    }
}
