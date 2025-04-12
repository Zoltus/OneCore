package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.listeners.InvseeHandler;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import org.bukkit.OfflinePlayer;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;

public class EnderChest implements ICommand {
    @Override
    public void init() {
        //enderchest <player>
        Argument<?> arg0 = new OfflinePlayerArgument()
                .withPermission(ENDER_CHEST_PERMISSION_OTHER.asPermission())
                .executesPlayer((sender, args) -> {
                    OfflinePlayer offTarget = (OfflinePlayer) args.get(0);
                    InvseeHandler.openInventory(sender, offTarget, true);
                });
        //enderchest
        new Command(ENDER_CHEST_LABEL)
                .withPermission(ENDER_CHEST_PERMISSION)
                .withAliases(ENDERCHEST_ALIASES)
                .executesPlayer((sender, args) -> {
                    sender.openInventory(sender.getEnderChest());
                }).then(arg0)
                .override();
    }
}
