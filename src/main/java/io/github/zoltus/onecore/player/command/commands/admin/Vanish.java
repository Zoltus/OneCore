package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.PlayerArgument;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class Vanish implements ICommand, Listener {

    @Getter
    private static final Set<UUID> vanished = new HashSet<>();

    @Override
    public void init() {
        //vanish <player>
        Argument<Player> arg0 = new PlayerArgument()
                .withPermission(Commands.VANISH_PERMISSION_OTHER.asPermission())
                .executes((sender, args) -> {
                    executes(sender, (Player) args.get(0));
                });
        //vanish
        new Command(Commands.VANISH_LABEL)
                .withPermission(Commands.VANISH_PERMISSION)
                .withAliases(Commands.VANISH_ALIASES)
                .executesPlayer((p, args) -> {
                    executes(p, p);
                }).then(arg0)
                .override();
    }

    private void executes(CommandSender sender, Player target) {
        User user = User.of(target);
        user.setVanished(!user.isVanished());
        boolean vanished = user.isVanished();

        //Sets player vanished for everyone who doesnt have the permission to see vanished players
        Bukkit.getOnlinePlayers().forEach(viewer -> {
            if (viewer != target && !canSeeVanished(viewer)) {
                if (vanished) {
                    viewer.hidePlayer(target);
                } else {
                    viewer.showPlayer(target);
                }
            }
        });

        String vanishedString = vanished ? VANISH_INVISIBLE.getString() : VANISH_VISIBLE.getString();

        if (target == sender) {
            VANISH_SELF.send(sender, MODE_PH, vanishedString);
        } else {
            VANISH_SELF.send(target, MODE_PH, vanishedString);
            VANISH_OTHER.send(sender, PLAYER_PH, target.getName(), MODE_PH, vanishedString);
        }
    }

    public static boolean canSeeVanished(Player viewer) {
        return viewer.hasPermission(Commands.VANISH_PERMISSION.asPermission())
                || viewer.hasPermission(Commands.VANISH_PERMISSION_OTHER.asPermission());
    }
}
