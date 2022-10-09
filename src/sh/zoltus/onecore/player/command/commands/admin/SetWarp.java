package sh.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.StringArgument;
import sh.zoltus.onecore.data.configuration.OneYml;
import sh.zoltus.onecore.data.configuration.Yamls;
import sh.zoltus.onecore.player.command.Command;
import sh.zoltus.onecore.player.command.IOneCommand;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.NODES_WARP_NAME;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.SETWARP_SET;

public class SetWarp implements IOneCommand {

    private final OneYml warps = Yamls.WARPS.getYml();

    @Override
    public void init() {
        //setwarp <warp>
        new Command(SETWARP_LABEL)
                .withPermission(SETWARP_PERMISSION)
                .withAliases(SETWARP_ALIASES)
                .then(new StringArgument(NODES_WARP_NAME.getString())
                .executesPlayer((p, args) -> {
                    String warp = (String) args[0];
                    warps.set(warp, p.getLocation());
                    warps.save();
                    warps.reload();
                    p.sendMessage(SETWARP_SET.rp(WARP_PH, warp));
                })).override();
        //todo /setwarp <warp> <coords> new LocationArgument("location")
    }
}
