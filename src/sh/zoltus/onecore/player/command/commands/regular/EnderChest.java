package sh.zoltus.onecore.player.command.commands.regular;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import sh.zoltus.onecore.listeners.InvSeeHandler;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;

public class EnderChest implements IOneCommand, Listener {
    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                //enderchest
                command(EnderChest_LABEL)
                        .withPermission(EnderChest_PERMISSION)
                        .withAliases(EnderChest_ALIASES)
                        .executesPlayer((sender, args) -> {
                    sender.openInventory(sender.getEnderChest());
                }),
                //enderchest <player>
                command(EnderChest_LABEL)
                        .withPermission(EnderChest_OTHER_PERMISSION)
                        .withAliases(EnderChest_ALIASES)
                        .withArguments(new OfflinePlayerArgument())
                        .executesPlayer((sender, args) -> {
                    OfflinePlayer offTarget = (OfflinePlayer) args[0];
                    InvSeeHandler.handle(sender, offTarget, true);
                })
        };
    }


}
