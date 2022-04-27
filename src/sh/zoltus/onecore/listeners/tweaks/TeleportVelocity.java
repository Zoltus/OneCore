package sh.zoltus.onecore.listeners.tweaks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import sh.zoltus.onecore.configuration.yamls.Config;

public class TeleportVelocity implements Listener {
    /**
     * Stops taking Fall Damage when teleporting
     *
     * @param e Event
     */
    @EventHandler
    public void onjoin(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        PlayerTeleportEvent.TeleportCause cause = e.getCause();
        if ((cause == PlayerTeleportEvent.TeleportCause.PLUGIN
                || cause == PlayerTeleportEvent.TeleportCause.COMMAND)
                && Config.TELEPORT_VELOCITY_RESET.getBoolean()) {
            p.setVelocity(p.getVelocity().zero());
        }
    }
}
