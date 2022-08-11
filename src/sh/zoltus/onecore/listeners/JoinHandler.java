package sh.zoltus.onecore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import sh.zoltus.onecore.data.database.Database;
import sh.zoltus.onecore.player.command.User;

import static sh.zoltus.onecore.data.configuration.IConfig.PLAYER_PH;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.JOINED;

public record JoinHandler() implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(AsyncPlayerPreLoginEvent e) {
        AsyncPlayerPreLoginEvent.Result result = e.getLoginResult();
        if (result == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            OfflinePlayer offP = Bukkit.getOfflinePlayer(e.getUniqueId());
            if (!User.getUsers().containsKey(offP.getUniqueId())) {
                if (!Database.database().loadPlayer(offP)) { //If user isnt on db it creates new one
                    new User(offP);
                }
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
        e.setJoinMessage(JOINED.rp(PLAYER_PH, p.getName()));
    }
}
