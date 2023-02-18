package io.github.zoltus.onecore.listeners.tweaks;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import io.github.zoltus.onecore.data.configuration.yamls.Config;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.KICKED_FOR_SPAMMING;

public class KickedForSpamming implements Listener {
    /**
     * Disabled Kicked for spamming kick event. testcommit
     *
     * @param e Event
     *          permission "bypass.spam"
     */
    @EventHandler
    public void onKick(PlayerKickEvent e) {
        if (e.getReason().equals("Kicked for spamming")) {
            if (e.getPlayer().hasPermission(Config.KICKED_FOR_SPAMMING_BYPASS.asPermission())) {
                e.setCancelled(true);
            } else {
                e.setReason(KICKED_FOR_SPAMMING.getString());
            }
        }
    }
}
