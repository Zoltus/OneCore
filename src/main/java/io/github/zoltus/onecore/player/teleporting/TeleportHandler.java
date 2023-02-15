package io.github.zoltus.onecore.player.teleporting;

import io.github.zoltus.onecore.player.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.TP_CANCELLED_BY_DAMAGE;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.TP_CANCELLED_BY_MOVEMENT;

public class TeleportHandler implements Listener {
    // Cancels Teleport on Move
    @EventHandler
    public static void onMove(PlayerMoveEvent e) {
        Location fromLoc = e.getFrom();
        Location toLoc = e.getTo();
        User user = User.of(e.getPlayer());
        if (toLoc != null
                && user.getTeleport() != null
                && !fromLoc.getBlock().equals(toLoc.getBlock())) {
            user.getTeleport().cancel(TP_CANCELLED_BY_MOVEMENT.getString());
        }
    }

    // Cancels if damaged
    @EventHandler
    public static void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            User user = User.of(p);
            user.getTeleport().cancel(TP_CANCELLED_BY_DAMAGE.getString());
        }
    }
}
