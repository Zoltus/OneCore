package io.github.zoltus.onecore.listeners;

import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public record JoinListener(OneCore plugin) implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(AsyncPlayerPreLoginEvent e) {
        AsyncPlayerPreLoginEvent.Result result = e.getLoginResult();
        if (result == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            OfflinePlayer offP = Bukkit.getOfflinePlayer(e.getUniqueId());
            if (User.of(offP) == null) {  //If user isnt in map or in the database it creates new one
                new User(offP);
            }
        }
    }

    /**
     * First join adds money
     *
     * @param e event
     */
    @EventHandler
    public void onLeave(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        e.setJoinMessage(Lang.JOINED.rp(IConfig.PLAYER_PH, p.getName()));
    }
}
