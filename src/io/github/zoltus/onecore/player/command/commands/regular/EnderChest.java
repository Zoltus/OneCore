package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.ArgumentTree;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import org.bukkit.OfflinePlayer;
import io.github.zoltus.onecore.listeners.InvSeeHandler;
import io.github.zoltus.onecore.player.command.Command;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;

public class EnderChest implements ICommand {
    @Override
    public void init() {
        //enderchest <player>
        ArgumentTree arg0 = new OfflinePlayerArgument()
                .executesPlayer((sender, args) -> {
                    OfflinePlayer offTarget = (OfflinePlayer) args[0];
                    InvSeeHandler.handle(sender, offTarget, true);
                });
        //enderchest
        new Command(ENDER_CHEST_LABEL)
                .withPermission(ENDER_CHEST_OTHER_PERMISSION)
                .withAliases(ENDERCHEST_ALIASES)
                .executesPlayer((sender, args) -> {
                    sender.openInventory(sender.getEnderChest());
                }).then(arg0)
                .override();
    }
}
