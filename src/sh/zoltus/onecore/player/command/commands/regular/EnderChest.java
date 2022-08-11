package sh.zoltus.onecore.player.command.commands.regular;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import sh.zoltus.onecore.listeners.InvSeeHandler;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.*;

public class EnderChest implements IOneCommand, Listener {
    @Override
    public void init() {
        //enderchest
        command(EnderChest_LABEL)
                .withPermission(EnderChest_PERMISSION)
                .withAliases(EnderChest_ALIASES)
                .executesPlayer((sender, args) -> {
                    sender.openInventory(sender.getEnderChest());
                }).override();
        //enderchest <player>
        command(EnderChest_LABEL)
                .withPermission(EnderChest_OTHER_PERMISSION)
                .withAliases(EnderChest_ALIASES)
                .withArguments(new OfflinePlayerArgument())
                .executesPlayer((sender, args) -> {
                    OfflinePlayer offTarget = (OfflinePlayer) args[0];
                    InvSeeHandler.handle(sender, offTarget, true);
                }).register();
    }


}
