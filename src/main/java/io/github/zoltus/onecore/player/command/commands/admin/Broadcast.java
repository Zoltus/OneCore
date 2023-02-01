package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.ChatArgument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;

public class Broadcast implements ICommand {

    @Override
    public void init() {
        new Command(Commands.BROADCAST_LABEL)
                .withPermission(Commands.BROADCAST_PERMISSION)
                .withAliases(Commands.BROADCAST_ALIASES)
                .then(new ChatArgument(Lang.NODES_MESSAGE.getString())
                        .executes((sender, args) -> {
                            //todo String PREFIX = Lang.BROADCAST_PREFIX.getString()
                            Bukkit.spigot().broadcast((BaseComponent[]) args.get(0));
                        })
                ).override();
    }
}
