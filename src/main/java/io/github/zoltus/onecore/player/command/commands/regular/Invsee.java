package io.github.zoltus.onecore.player.command.commands.regular;

import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import io.github.zoltus.onecore.listeners.InvSeeHandler;
import org.bukkit.OfflinePlayer;
import io.github.zoltus.onecore.player.command.Command;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;

public class Invsee implements ICommand {
    //Todo armorcontents
    @Override
    public void init() {
        //invsee <player>
        new Command(INVSEE_LABEL)
                .withPermission(INVSEE_PERMISSION)
                .withAliases(INVSEE_ALIASES)
                .then(new OfflinePlayerArgument()
                        .executesPlayer((sender, args) -> {
                            OfflinePlayer offTarget = (OfflinePlayer) args.get(0);
                            InvSeeHandler.handle(sender, offTarget, false);
                        })).override();
    }
}
