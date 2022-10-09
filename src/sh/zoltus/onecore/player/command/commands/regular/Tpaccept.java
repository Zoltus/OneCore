package sh.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.ArgumentTree;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.Command;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.User;
import sh.zoltus.onecore.player.command.arguments.RequestArgument;
import sh.zoltus.onecore.player.teleporting.Request;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.TP_NO_REQUESTS;

public class Tpaccept implements IOneCommand {


    @Override
    public void init() {
        //tpaccept <player>
        ArgumentTree arg0 = new RequestArgument()
                .executesPlayer((player, args) -> {
                    handle((Player) args[0], player);
                });
        //tpaccept
        new Command(TPACCEPT_LABEL)
                .withPermission(TPACCEPT_PERMISSION)
                .withAliases(TPACCEPT_ALIASES)
                .executesPlayer((player, args) -> {
                    handle(player, player);
                }).then(arg0)
                .override();
    }

    private void handle(Player target, Player player) {
        Request latest = Request.getLatest(User.of(target));
        if (latest == null) {
            player.sendMessage(TP_NO_REQUESTS.getString());
        } else {
            latest.accept();
        }
    }
}
