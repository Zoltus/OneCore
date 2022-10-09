package sh.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.ArgumentTree;
import org.bukkit.OfflinePlayer;
import sh.zoltus.onecore.listeners.InvSeeHandler;
import sh.zoltus.onecore.player.command.Command;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;

public class EnderChest implements IOneCommand {
    @Override
    public void init() {
        //enderchest <player>
        ArgumentTree arg0 = new OfflinePlayerArgument()
                .executesPlayer((sender, args) -> {
                    OfflinePlayer offTarget = (OfflinePlayer) args[0];
                    InvSeeHandler.handle(sender, offTarget, true);
                });
        //enderchest
        new Command(EnderChest_LABEL)
                .withPermission(EnderChest_OTHER_PERMISSION)
                .withAliases(EnderChest_ALIASES)
                .executesPlayer((sender, args) -> {
                    sender.openInventory(sender.getEnderChest());
                }).then(arg0)
                .override();
    }
}
