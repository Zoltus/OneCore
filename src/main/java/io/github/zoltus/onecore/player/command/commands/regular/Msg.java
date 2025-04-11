package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ChatArgument;
import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.PlayerArgument;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Msg implements ICommand {
    //todo /r response
    @Override
    public void init() {
        //msg <player> <message>
        Argument<?> arg0 = new PlayerArgument();
        Argument<?> arg1 = new ChatArgument(Lang.NODES_MESSAGE.get())
                .executesPlayer((sender, args) -> {
                    Player target = (Player) args.get(0);
                    String senderName = sender.getName();
                    String targetName = target.getName();
                    String message = BaseComponent.toPlainText((BaseComponent[]) args.get(1));
                    message = ChatColor.translateAlternateColorCodes('&', message);
                    Lang.MSG_SENT_MSG
                            .rb(IConfig.PLAYER_PH, targetName)
                            .rb(IConfig.PLAYER2_PH, senderName)
                            .rb(IConfig.MESSAGE_PH, message).send(sender);
                    Lang.MSG_RECEIVED_MSG
                            .rb(IConfig.PLAYER_PH, senderName)
                            .rb(IConfig.PLAYER2_PH, targetName)
                            .rb(IConfig.MESSAGE_PH, message).send(target);
                });
        //base
        new Command(Commands.MSG_LABEL)
                .withPermission(Commands.MSG_PERMISSION)
                .withAliases(Commands.MSG_ALIASES)
                .then(arg0.then(arg1))
                .override();
    }
}
