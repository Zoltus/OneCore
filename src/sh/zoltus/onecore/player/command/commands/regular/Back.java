package sh.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.User;
import sh.zoltus.onecore.player.command.arguments.PlayerArgument;

import java.util.List;

import static sh.zoltus.onecore.data.configuration.yamls.Config.BACK_HISTORY_SIZE;
import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.*;

public class Back implements IOneCommand, Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onTeleport(PlayerTeleportEvent e) {
        User user = User.of(e.getPlayer());
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

    @Override
    public void init() {
        // back
        command(BACK_LABEL)
                .withPermission(BACK_PERMISSION)
                .withAliases(BACK_ALIASES)
                .executesPlayer((sender, args) -> {
                    executes(sender, 1, sender);
                }).override();
        // back <amount>
        command(BACK_LABEL)
                .withPermission(BACK_PERMISSION)
                .withAliases(BACK_ALIASES)
                .withArguments(backArg())
                .executesPlayer((sender, args) -> {
                    executes(sender, (int) args[0], sender);
                }).register();
        // back <amount> <player>
        command(BACK_LABEL)
                .withPermission(BACK_PERMISSION)
                .withAliases(BACK_ALIASES)
                .withArguments(backArg(), new PlayerArgument())
                .executes((sender, args) -> executes(sender, (int) args[0], (Player) args[1]))
                .register();
               /* // back
        new CommandTree(BACK_LABEL.getString())
                .withPermission(BACK_PERMISSION.getString())
                .withAliases(BACK_ALIASES.getString())
                .executesPlayer((sender, args) -> {
                    executes(sender, 1, sender);
                })
                // back <amount>
                .then(backArg()
                        .executesPlayer((sender, args) -> {
                            executes(sender, (int) args[0], sender);
                        })
                        // back <amount> <player>
                        .then(new PlayerArgument()
                                .executes((CommandExecutor) (sender, args) -> executes(sender, (int) args[0], (Player) args[1])))
                ).override();*/
    }

    private Argument<?> backArg() {
        return new CustomArgument<>(new StringArgument("1-" + BACK_HISTORY_SIZE.getInt()), (info) -> {
            try {
                return Integer.parseInt(info.input());
            } catch (Exception e) {
                throw new CustomArgument.CustomArgumentException(BACK_INVALID_NUMBER.getString());
            }
        });
    }

    private void executes(CommandSender sender, int backAmount, Player target) {
        User targetUser = User.of(target);
        if (targetUser.getLastLocations().isEmpty()) {
            sender.sendMessage(BACK_NO_HISTORY.rp(PLAYER_PH, target.getName()));
        } else if (backAmount > targetUser.getLastLocations().size()) {
            sender.sendMessage(BACK_OUT_OF_BOUNDS.rp(SIZE_PH, targetUser.getLastLocations().size()));
        } else if (sender != target) {
            sender.sendMessage(BACK_TARGET_SENT.rp(PLAYER_PH, target.getName()));
            target.teleport(targetUser.getLastLocation(backAmount));
        } else if (sender.hasPermission(COOLDOWN_BYPASS_PERMISSION.getString())) {
            target.teleport(targetUser.getLastLocation(backAmount));
        } else {
            targetUser.teleportTimer(targetUser.getLastLocation(backAmount));
        }
    }
}
