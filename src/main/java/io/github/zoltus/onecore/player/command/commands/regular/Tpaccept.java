package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.ArgumentTree;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.RequestArgument;
import io.github.zoltus.onecore.player.teleporting.Request;
import org.bukkit.entity.Player;
import io.github.zoltus.onecore.player.User;

public class Tpaccept implements ICommand {
    @Override
    public void init() {
        //tpaccept <player>
        ArgumentTree arg0 = new RequestArgument()
                .executesPlayer((player, args) -> {
                    handle((Player) args[0], player);
                });
        //tpaccept
        new Command(Commands.TPACCEPT_LABEL)
                .withPermission(Commands.TPACCEPT_PERMISSION)
                .withAliases(Commands.TPACCEPT_ALIASES)
                .executesPlayer((player, args) -> {
                    handle(player, player);
                }).then(arg0)
                .override();
    }

    private void handle(Player target, Player player) {
        Request latest = Request.getLatest(User.of(target));
        if (latest == null) {
            player.sendMessage(Lang.TP_NO_REQUESTS.getString());
        } else {
            latest.accept();
        }
    }
}
