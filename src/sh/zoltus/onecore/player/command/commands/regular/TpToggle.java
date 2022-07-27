package sh.zoltus.onecore.player.command.commands.regular;

import sh.zoltus.onecore.player.command.IOneCommand;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.TP_TOGGLE_SWITCHED;

public class TpToggle implements IOneCommand {

    @Override
    public void init() {
        //Tptoggle
        command(TPTOGGLE_LABEL)
                .withPermission(TPTOGGLE_PERMISSION)
                .withAliases(TPTOGGLE_ALIASES)
                .executesUser((user, args) -> {
                    user.setTpEnabled(!user.isTpEnabled());
                    user.sendMessage(TP_TOGGLE_SWITCHED.rp(TOGGLE_PH, user.isTpEnabled()));
                }).register();
    }
}
