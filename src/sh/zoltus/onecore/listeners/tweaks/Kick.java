package sh.zoltus.onecore.listeners.tweaks;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import sh.zoltus.onecore.configuration.yamls.Config;

import static sh.zoltus.onecore.configuration.yamls.Lang.KICKED_FOR_SPAMMING;

public class Kick implements Listener {
    /**
     * Disabled Kicked for spamming kick event.
     *
     * @param e Event
     * @Permission "bypass.spam"
     */
    @EventHandler
    public void onKick(PlayerKickEvent e) {
        if (e.getReason().equals("Kicked for spamming")) {
            if (!e.getPlayer().hasPermission(Config.KICKED_FOR_SPAMMING_BYPASS.getAsPermission())) {
                e.setReason(KICKED_FOR_SPAMMING.getString());
            }
        }

    }
}
