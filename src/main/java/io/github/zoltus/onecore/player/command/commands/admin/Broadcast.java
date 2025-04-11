package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.ChatArgument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

public class Broadcast implements ICommand {

    @Override
    public void init() {
        new Command(Commands.BROADCAST_LABEL)
                .withPermission(Commands.BROADCAST_PERMISSION)
                .withAliases(Commands.BROADCAST_ALIASES)
                .then(new ChatArgument(Lang.NODES_MESSAGE.get())
                        .executes((sender, args) -> {
                            BaseComponent[] prefix = TextComponent.fromLegacyText(Lang.BROADCAST_PREFIX.get());
                            BaseComponent[] message = (BaseComponent[]) args.get(0);
                            ComponentBuilder builder = new ComponentBuilder();
                            builder.append(prefix).append(message);
                            Bukkit.spigot().broadcast(builder.create());
                        })
                ).override();
    }
}
