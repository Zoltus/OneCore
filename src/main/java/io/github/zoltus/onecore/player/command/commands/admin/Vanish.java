package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.PlayerArgument;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class Vanish implements ICommand {

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

        //Refresh vanished players action bar every 2.5s same for godmode?
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,
                () -> User.getUsers().values().forEach(this::sendActionBar),0,50);
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
        sendActionBar(user);
    }

    public static boolean canSeeVanished(Player viewer) {
        return viewer.hasPermission(Commands.VANISH_PERMISSION.asPermission())
                || viewer.hasPermission(Commands.VANISH_PERMISSION_OTHER.asPermission());
    }

    private void sendActionBar(User user) {
        if (user.isOnline() && user.isVanished()) {
            Player player = user.getPlayer();
            String actionbar = VANISH_INVISIBLE_ACTION_BAR.getString();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionbar));
        }
    }
}
