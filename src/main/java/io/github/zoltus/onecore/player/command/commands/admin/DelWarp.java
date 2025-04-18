package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.data.configuration.OneYml;
import io.github.zoltus.onecore.data.configuration.Yamls;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.WarpArgument;
import io.github.zoltus.onecore.player.command.commands.regular.Warp;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;
import static io.github.zoltus.onecore.data.configuration.PlaceHolder.*;

public class DelWarp implements ICommand {
    private final OneYml warps = Yamls.WARPS.getYml();

    @Override
    public void init() {
        //delwarp <warp>
        Argument<?> arg0 = new WarpArgument()
                .executes((sender, args) -> {
                    Warp.WarpObj warp = (Warp.WarpObj) args.get(0);
                    Lang.DELWARP_DELETED.rb(WARP_PH, warp.name()).send(sender);
                    warps.set(warp.name(), null);
                    warps.save();
                    warps.reload();
                });
        new Command(DELWARP_LABEL)
                .withPermission(DELWARP_PERMISSION)
                .withAliases(DELWARP_ALIASES)
                .then(arg0)
                .override();
    }
}




















