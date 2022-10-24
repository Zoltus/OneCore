package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.ChatArgument;
import dev.jorel.commandapi.wrappers.PreviewLegacy;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import io.github.zoltus.onecore.utils.ChatUtils;

public class Broadcast implements ICommand {

    private final String PREFIX = Lang.BROADCAST_PREFIX.getString();

    @Override
    public void init() {
        new Command(Commands.BROADCAST_LABEL)
                .withPermission(Commands.BROADCAST_PERMISSION)
                .withAliases(Commands.BROADCAST_ALIASES)
                .then(new ChatArgument(Lang.NODES_MESSAGE.getString())
                        .usePreview(true)
                        .withPreview((PreviewLegacy) info ->
                                ChatUtils.toComponents(PREFIX + info.input()))
                        .executes((sender, args) -> {
                            Bukkit.spigot().broadcast((BaseComponent[]) args[0]);
                        })
                ).override();
    }
}
