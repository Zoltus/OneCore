package io.github.zoltus.onecore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static io.github.zoltus.onecore.data.configuration.PlaceHolder.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.QUIT;

public class QuitListener implements Listener {

    /**
     * Saves & removes user
     */
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        e.setQuitMessage(QUIT.rb(PLAYER_PH, p.getName()).buildLegacyString());
    }
}
