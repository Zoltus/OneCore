package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.PlayerArgument;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;
import static io.github.zoltus.onecore.data.configuration.PlaceHolder.*;

public class Back implements ICommand, Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        User user = User.of(p);
        //"NPC" fixes citizens stuff
        if (p.hasMetadata("NPC")) return;
        user.setLastLocation(e.getFrom());
    }


    @EventHandler
    public void onEntityDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (!p.hasMetadata("NPC")) {
            User user = User.of(p);
            user.setLastLocation(p.getLocation());
        }
    }

    @Override
    public void init() {
        // back <player>
        Argument<Player> other = new PlayerArgument()
                .withPermission(BACK_PERMISSION_OTHER.asPermission())
                .executes((sender, args) -> {
                    executes(sender, (Player) args.get(0));
                });
        // back
        new Command(BACK_LABEL)
                .withPermission(BACK_PERMISSION)
                .withAliases(BACK_ALIASES)
                .executesPlayer((sender, args) -> {
                    executes(sender, sender);
                })
                .then(other)
                .override();
    }

    private void executes(CommandSender sender, Player target) {
        User targetUser = User.of(target);
        Location loc = targetUser.getLastLocation();
        if (loc == null) {
            BACK_NO_HISTORY.rb(PLAYER_PH, target.getName()).send(sender);
        } else if (sender != target) {
            BACK_TARGET_SENT.rb(PLAYER_PH, target.getName()).send(sender);
            target.teleport(targetUser.getLastLocation());
        } else {
            targetUser.teleport(targetUser.getLastLocation());
        }
    }
}
