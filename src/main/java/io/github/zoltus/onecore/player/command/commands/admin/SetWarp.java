package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.data.configuration.OneYml;
import io.github.zoltus.onecore.data.configuration.Yamls;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class SetWarp implements ICommand {

    @Override
    public void init() {
        //setwarp <warp>
        new Command(Commands.SETWARP_LABEL)
                .withPermission(Commands.SETWARP_PERMISSION)
                .withAliases(Commands.SETWARP_ALIASES)
                .then(new StringArgument(NODES_WARP_NAME.get())
                .executesPlayer((p, args) -> {
                    String warp = (String) args.get(0);
                    OneYml warps = Yamls.WARPS.getYml();
                    warps.set(warp, p.getLocation());
                    warps.save();
                    warps.reload();
                    SETWARP_SET.rb(WARP_PH, warp).send(p);
                })).override();
        //todo /setwarp <warp> <coords> new LocationArgument("location")
    }
}
