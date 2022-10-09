package sh.zoltus.onecore.player.command.commands.regular;

import sh.zoltus.onecore.player.User;
import sh.zoltus.onecore.player.command.Command;
import sh.zoltus.onecore.player.command.IOneCommand;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.TP_TOGGLE_SWITCHED;

public class TpToggle implements IOneCommand {

    @Override
    public void init() {
        //Tptoggle
        new Command(TPTOGGLE_LABEL)
                .withPermission(TPTOGGLE_PERMISSION)
                .withAliases(TPTOGGLE_ALIASES)
                .executesPlayer((player, args) -> {
                    User user = User.of(player);
                    user.setTpEnabled(!user.isTpEnabled());
                    user.sendMessage(TP_TOGGLE_SWITCHED.rp(TOGGLE_PH, user.isTpEnabled()));
                })
                .override();
    }
}
