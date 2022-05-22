package sh.zoltus.onecore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import sh.zoltus.onecore.database.Database;
import sh.zoltus.onecore.player.command.User;

import static sh.zoltus.onecore.configuration.IConfig.PLAYER_PH;
import static sh.zoltus.onecore.configuration.yamls.Lang.JOINED;

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


    static double x,y,z = 0;

    @EventHandler
    public void onLeave(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Vector v = p.getVelocity();
        double x1 = v.getX();
        double y1 = v.getY();
        double z1 = v.getZ();
        if (x1 > x ) {
            x = x1;
            Bukkit.broadcastMessage(x1 + "");
        }
        if (y1 > y ) {
            y = y1;
            Bukkit.broadcastMessage(y1+ "");
        }
        if (z1 > z ) {
            z = z1;
            Bukkit.broadcastMessage(z1+ "");
        }
        if (p.isSneaking()) {
            p.sendMessage("x: " + x + " y: " + y + " z: " + z);
        }


    }
}
