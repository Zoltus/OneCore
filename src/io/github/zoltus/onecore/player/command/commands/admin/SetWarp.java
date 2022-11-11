package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.OneYml;
import io.github.zoltus.onecore.data.configuration.Yamls;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;

import static io.github.zoltus.onecore.data.configuration.IConfig.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class SetWarp implements ICommand {

    private final OneYml warps = Yamls.WARPS.getYml();

    @Override
    public void init() {
        //setwarp <warp>
        new Command(Commands.SETWARP_LABEL)
                .withPermission(Commands.SETWARP_PERMISSION)
                .withAliases(Commands.SETWARP_ALIASES)
                .then(new StringArgument(NODES_WARP_NAME.getString())
                .executesPlayer((p, args) -> {
                    String warp = (String) args[0];
                    warps.set(warp, p.getLocation());
                    warps.save();
                    warps.reload();
                    SETWARP_SET.send(p, WARP_PH, warp);
                })).override();
        //todo /setwarp <warp> <coords> new LocationArgument("location")
    }
}
