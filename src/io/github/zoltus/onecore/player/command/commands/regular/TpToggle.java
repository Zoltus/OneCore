package io.github.zoltus.onecore.player.command.commands.regular;

import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.Command;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;

public class TpToggle implements ICommand {

    @Override
    public void init() {
        //Tptoggle
        new Command(TPTOGGLE_LABEL)
                .withPermission(TPTOGGLE_PERMISSION)
                .withAliases(TPTOGGLE_ALIASES)
                .executesPlayer((player, args) -> {
                    User user = User.of(player);
                    user.setTpEnabled(!user.isTpEnabled());
                    Lang.TP_TOGGLE_SWITCHED.send(user, TOGGLE_PH, user.isTpEnabled());
                })
                .override();
    }
}
