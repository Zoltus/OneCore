package sh.zoltus.onecore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static sh.zoltus.onecore.data.configuration.IConfig.PLAYER_PH;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.JOINED;

public record JoinListener() implements Listener {

    /**
     * First join adds money
     *
     * @param e event
     */
    @EventHandler
    public void onLeave(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        e.setJoinMessage(JOINED.rp(PLAYER_PH, p.getName()));
    }
}
