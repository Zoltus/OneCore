package sh.zoltus.onecore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static sh.zoltus.onecore.data.configuration.yamls.Lang.PLAYER_PH;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.QUIT;

public class QuitListener implements Listener {

    /**
     * Saves & removes user
     */
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        e.setQuitMessage(QUIT.rp(PLAYER_PH, p.getName()));
    }
}
