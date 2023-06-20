package io.github.zoltus.onecore.listeners;

import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.commands.admin.Vanish;
import io.github.zoltus.onecore.player.command.commands.regular.Spawn;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public record JoinListener(OneCore plugin) implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleUserOnJoin(AsyncPlayerPreLoginEvent e) {
        AsyncPlayerPreLoginEvent.Result result = e.getLoginResult();
        if (result == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            OfflinePlayer offP = Bukkit.getOfflinePlayer(e.getUniqueId());
            if (User.of(offP) == null) {
                new User(offP);
            }
        }
    }

    //Loads onlineplayers if they didnt exists on database
    //Adds better support for loading plugin midgame
    public static void loadOnlinePlayers() {
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (User.of(p) == null) {
                new User(p);
            }
        });
    }

    /**
     * First join adds money
     *
     * @param e event
     */
    @EventHandler
    public void handleSpawnOnJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        e.setJoinMessage(Lang.JOINED.replace(IConfig.PLAYER_PH, p.getName()));
        //ForceSpawns
        if (Config.TELEPORT_SPAWN_ON_JOIN.getBoolean()) {
            Location spawn = Spawn.getSpawn();
            if (spawn == null) {
                p.sendMessage(Lang.SPAWN_IS_NOT_SET.getString());
            } else {
                p.teleport(spawn);
            }
        }

        //If first join it teleports to firstjoinspawn
        if (!p.hasPlayedBefore()) {
            Location firstJoinSpawn = Spawn.getFirstJoinSpawn();
            if (firstJoinSpawn != null) {
                p.teleport(firstJoinSpawn);
            }
        }
    }

    //todo cleanup
    @EventHandler(priority = EventPriority.MONITOR)
    public void handleVanishOnJoin(PlayerJoinEvent e) {
        Player joinerPlayer = e.getPlayer();
        User joiner = User.of(joinerPlayer);
        Bukkit.getOnlinePlayers().forEach(viewer -> {
            User user = User.of(viewer);
            if ((!user.isVanished() || Vanish.canSeeVanished(joinerPlayer))
                    && (!joiner.isVanished() || Vanish.canSeeVanished(viewer))) {
                return;
            }
            viewer.hidePlayer(joinerPlayer);
            joinerPlayer.hidePlayer(viewer);
        });
    }
}
