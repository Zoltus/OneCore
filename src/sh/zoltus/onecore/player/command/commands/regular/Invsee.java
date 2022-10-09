package sh.zoltus.onecore.player.command.commands.regular;

import org.bukkit.OfflinePlayer;
import sh.zoltus.onecore.listeners.InvSeeHandler;
import sh.zoltus.onecore.player.command.Command;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;

public class Invsee implements IOneCommand {
    //Todo armorcontents
    @Override
    public void init() {
        //invsee <player>
        new Command(INVSEE_LABEL)
                .withPermission(INVSEE_PERMISSION)
                .withAliases(INVSEE_ALIASES)
                .then(new OfflinePlayerArgument()
                        .executesPlayer((sender, args) -> {
                            OfflinePlayer offTarget = (OfflinePlayer) args[0];
                            InvSeeHandler.handle(sender, offTarget, false);
                        })).override();
    }
}
