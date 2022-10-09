package sh.zoltus.onecore.player.command.commands.regular;

import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.User;
import sh.zoltus.onecore.player.command.Command;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.PlayerArgument;
import sh.zoltus.onecore.player.teleporting.Request;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;

public class Tpa implements IOneCommand {

    @Override
    public void init() {
        //tpa <player>
        new Command(TPA_LABEL)
                .withPermission(TPA_PERMISSION)
                .withAliases(TPA_ALIASES)
                .then(new PlayerArgument()
                        .executesPlayer((player, args) -> {
                            User user = User.of(player);
                            User target = User.of((Player) args[0]);
                            Request.send(user, target, Request.TeleportType.TPA);
                        })).override();
    }
}
