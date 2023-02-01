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
        Argument<?> arg1 = new ChatArgument(Lang.NODES_MESSAGE.getString())
                .executesPlayer((sender, args) -> {
                    Player target = (Player) args.get(0);
                    String senderName = sender.getName();
                    String targetName = target.getName();
                    String message = BaseComponent.toPlainText((BaseComponent[]) args.get(1));
                    message = ChatColor.translateAlternateColorCodes('&', message);
                    sender.sendMessage(toSendMessage(targetName, senderName, message));
                    Lang.MSG_RECEIVED_MSG.send(target,
                            IConfig.PLAYER_PH, senderName,
                            IConfig.PLAYER2_PH, targetName,
                            IConfig.MESSAGE_PH, message);
                });
        //base
        new Command(Commands.MSG_LABEL)
                .withPermission(Commands.MSG_PERMISSION)
                .withAliases(Commands.MSG_ALIASES)
                .then(arg0.then(arg1))
                .override();
    }

    //??
    private String toSendMessage(String sender, String target, String message) {
        return Lang.MSG_SENT_MSG.replace(IConfig.PLAYER_PH, sender,
                IConfig.PLAYER2_PH, target,
                IConfig.MESSAGE_PH, message);
    }
}
