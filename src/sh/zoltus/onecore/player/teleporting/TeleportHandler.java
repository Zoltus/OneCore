package sh.zoltus.onecore.player.teleporting;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

import static sh.zoltus.onecore.configuration.yamls.Lang.*;

public class TeleportHandler implements Listener {

    public static boolean hasTeleport(UUID uuid) {
        return Teleport.getTeleports().containsKey(uuid);
    }

    public static Teleport getTeleport(UUID uuid) {
        return Teleport.getTeleports().get(uuid);
    }

    // Cancels Teleport on Move
    @EventHandler
    public static void onMove(PlayerMoveEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        Location fromLoc = e.getFrom();
        Location toLoc = e.getTo();
        if (toLoc != null && hasTeleport(uuid)) {
            if (!fromLoc.getBlock().equals(toLoc.getBlock())) {
                getTeleport(uuid).cancel(TP_CANCELLED_BY_MOVEMENT.getString());
            }
        }
    }

    // Cancels if damaged
    @EventHandler
    public static void onDamage(EntityDamageEvent e) {
        UUID uuid = e.getEntity().getUniqueId();
        if (e.getEntity() instanceof Player && hasTeleport(uuid)) {
            getTeleport(uuid).cancel(TP_CANCELLED_BY_DAMAGE.getString());
        }
    }

    // Cancel if left
    @EventHandler
    public static void onQuit(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (hasTeleport(uuid)) {
            Teleport tele = getTeleport(uuid);
            if (tele.getTarget() != null) {
                getTeleport(uuid).cancel(TP_TARGET_QUIT.rp(PLAYER_PH, tele.getTarget().getName()));
            }
        }
    }
}
