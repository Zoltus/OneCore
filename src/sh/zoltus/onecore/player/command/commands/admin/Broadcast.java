package sh.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.ChatArgument;
import dev.jorel.commandapi.wrappers.PreviewLegacy;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import sh.zoltus.onecore.player.command.IOneCommand;

import static sh.zoltus.onecore.data.configuration.yamls.Lang.BROADCAST_PREFIX;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.NODES_MESSAGE;
import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;

public class Broadcast implements IOneCommand {

    private final String PREFIX = BROADCAST_PREFIX.getString();

    @Override
    public void init() {
        command(BROADCAST_LABEL)
                .withPermission(BROADCAST_PERMISSION)
                .withAliases(BROADCAST_ALIASES)
                .withArguments(
                        new ChatArgument(NODES_MESSAGE.getString())
                                .usePreview(true)
                                .withPreview((PreviewLegacy) info -> toComponents(PREFIX + info.input())))
                .executes((sender, args) -> Bukkit.spigot().broadcast((BaseComponent[]) args[0])).override();
    }
}
