package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
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
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.PLAYER_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Commands.SIZE_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Config.BACK_HISTORY_SIZE;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class Back implements ICommand, Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        User user = User.of(p);
        //"NPC" fixes citizens stuff
        if (user != null && !p.hasMetadata("NPC")) {
            List<Location> lastLocations = user.getLastLocations();
            // If player does /Back it wont read the location to the backs where he goes
            if (!lastLocations.contains(e.getTo())) {
                // If player has max backs it removes the oldest saved loc
                if (lastLocations.size() == BACK_HISTORY_SIZE.getInt()) {
                    lastLocations.remove(0);
                }
                lastLocations.add(e.getFrom());
            }
        }
    }

    @Override
    public void init() {
        // back <amount>
        Argument<?> arg0 = backArg()
                .executesPlayer((sender, args) -> {
                    executes(sender, (int) args.get(0), sender);
                });
        // back <amount> <player>
        Argument<Player> arg1 = new PlayerArgument()
                .withPermission(BACK_OTHER_PERMISSION.asPermission())
                .executes((sender, args) -> {
                    executes(sender, (int) args.get(0), (Player) args.get(1));
                });
        // back
        new Command(BACK_LABEL)
                .withPermission(BACK_PERMISSION)
                .withAliases(BACK_ALIASES)
                .executesPlayer((sender, args) -> {
                    executes(sender, 1, sender);
                })
                .then(arg0.then(arg1))
                .override();
    }

    private Argument<?> backArg() {
        return new CustomArgument<>(new StringArgument("1-" + BACK_HISTORY_SIZE.getInt()), info -> {
            try {
                return Integer.parseInt(info.input());
            } catch (Exception e) {
                throw new CustomArgument.CustomArgumentException(BACK_INVALID_NUMBER.getString());
            }
        });
    }

    private void executes(CommandSender sender, int backAmount, Player target) {
        User targetUser = User.of(target);
        //permission check
        int maxBack = maxBack(target);
        if (maxBack < backAmount) {
           // todo BACK_
            target.sendMessage("§cYou don't have permission to go back that far (ADD TO YML)");
        } else if (targetUser.getLastLocations().isEmpty()) {
            BACK_NO_HISTORY.send(sender, PLAYER_PH, target.getName());
        } else if (backAmount > targetUser.getLastLocations().size()) {
            BACK_OUT_OF_BOUNDS.send(sender, SIZE_PH, targetUser.getLastLocations().size());
        } else if (sender != target) {
            BACK_TARGET_SENT.send(sender, PLAYER_PH, target.getName());
            target.teleport(targetUser.getLastLocation(backAmount));
        } else if (sender.hasPermission(COOLDOWN_BYPASS_PERMISSION.getString())) {
            target.teleport(targetUser.getLastLocation(backAmount));
        } else {
            targetUser.teleport(targetUser.getLastLocation(backAmount));
        }
    }

    public int maxBack(Player player) {
        String perm = BACK_OTHER_PERMISSION.asPermission() + ".";
        return player.getEffectivePermissions().stream()
                .filter(permission -> permission.getPermission().startsWith(perm))
                .map(permission -> Integer.parseInt(permission.getPermission().replace(perm, "")))
                .max(Integer::compareTo)
                .orElse(3);
        //Default history size todo
    }
}
