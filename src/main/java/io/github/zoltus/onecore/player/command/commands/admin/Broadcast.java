package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.ChatArgument;
import io.github.zoltus.onecore.data.configuration.LangBuilder;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Broadcast implements ICommand {

    @Override
    public void init() {
        new Command(Commands.BROADCAST_LABEL)
                .withPermission(Commands.BROADCAST_PERMISSION)
                .withAliases(Commands.BROADCAST_ALIASES)
                .then(new ChatArgument(Lang.NODES_MESSAGE.get())
                        .executes((sender, args) -> {
                            BaseComponent[] components = (BaseComponent[]) args.get(0);
                            String message = Lang.BROADCAST_PREFIX.get() + BaseComponent.toLegacyText(components);
                            LangBuilder langBuilder = new LangBuilder(message);
                            langBuilder.send(Bukkit.getOnlinePlayers().toArray(Player[]::new));
                        })
                ).override();
    }
}
