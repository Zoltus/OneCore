package sh.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.ChatArgument;
import dev.jorel.commandapi.wrappers.PreviewLegacy;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.BROADCAST_PREFIX;
import static sh.zoltus.onecore.configuration.yamls.Lang.NODES_MESSAGE;

public class Broadcast implements IOneCommand {

    private final String PREFIX = BROADCAST_PREFIX.getString();

    @Override
    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                command(BROADCAST_LABEL)
                        .withPermission(BROADCAST_PERMISSION)
                        .withAliases(BROADCAST_ALIASES)
                        .withArguments(new ChatArgument(NODES_MESSAGE.getString())
                                .withPreview((PreviewLegacy) info -> toComponents(PREFIX + info.input())))
                        .executes((sender, args) -> {
                    String message = BaseComponent.toPlainText((BaseComponent[]) args[0]);
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + message));
                })
        };
    }
}
