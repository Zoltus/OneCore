package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.HomeArg0;
import io.github.zoltus.onecore.player.command.arguments.HomeArg1;
import io.github.zoltus.onecore.utils.HomeLocation;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.HOME_DEFAULT_NAME;

public class Home implements ICommand {

    @Override
    public void init() {
        //home <home>
        Argument<?> homeArg0 = new HomeArg0() //String
                .executesPlayer((p, args) -> {
                    //todo? String home = (String) args.getOptional(0).orElse(Commands.HOME_DEFAULT_NAME.getString());
                    String home = (String) args.getOrDefault(0, Commands.HOME_DEFAULT_NAME.getString());
                    teleportHome(p, p, home);
                });
        //home <player> <home>
        Argument<?> homeArg1 = new HomeArg1() //
                .withPermission(Commands.HOME_PERMISSION_OTHER.asPermission())
                .executes((sender, args) -> {
                    OfflinePlayer offP = Bukkit.getOfflinePlayer((String) args.get(0));
                    String home = (String) args.getOrDefault(1, Commands.HOME_DEFAULT_NAME.getString());
                    teleportHome(sender, offP, home);
                });
        //home
        new Command(Commands.HOME_LABEL)
                .withPermission(Commands.HOME_PERMISSION)
                .withAliases(Commands.HOME_ALIASES)
                .executesPlayer((p, args) -> {
                    teleportHome(p, p, null);
                }).then(homeArg0.then(homeArg1))
                .override();
    }

    private void teleportHome(CommandSender sender, OfflinePlayer offP, String home) {
        User target = User.of(offP);
        if (target == null) {
            sender.sendMessage(Lang.PLAYER_NEVER_VISITED_SERVER.getString());
        } else {
            home = home == null ? HOME_DEFAULT_NAME.getString() : home.toLowerCase();
            HomeLocation loc = target.getHome(home);
            User user = User.of((Player) sender); //Cant be other than player since u cant tele others to their homes
            if (loc != null) {
                user.teleport(loc.toLocation());
            } else {
                Lang.HOME_LIST.send(sender, IConfig.LIST_PH, target.getHomes().keySet());
                return;
            }
            boolean isSelf = sender.getName().equals(offP.getName());
            if (!isSelf) {
                Lang.HOME_TELEPORT_OTHERS.send(sender, IConfig.PLAYER_PH, target.getName(), IConfig.HOME_PH, home);
            }
        }
    }
}




















