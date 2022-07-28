package sh.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.StringArgument;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.commands.regular.Warp;
import sh.zoltus.onecore.player.teleporting.PreLocation;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.NODES_WARP_NAME;
import static sh.zoltus.onecore.configuration.yamls.Lang.SETWARP_SET;

public class SetWarp implements IOneCommand {

    @Override
    public void init() {
                //setwarp <warp>
                command(SETWARP_LABEL)
                        .withPermission(SETWARP_PERMISSION)
                        .withAliases(SETWARP_ALIASES)
                        .withArguments(new StringArgument(NODES_WARP_NAME.getString()))
                        .executesPlayer((p, args) -> {
                    String warp = (String) args[0];
                    Warp.getWarps().put(warp, new PreLocation(p.getLocation()));
                    p.sendMessage(SETWARP_SET.rp(WARP_PH, warp));
                }).override();
                //todo /setwarp <warp> <coords> new LocationArgument("location")
    }
}
