package io.github.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.arguments.ChatArgument;
import dev.jorel.commandapi.wrappers.PreviewLegacy;
import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.PlayerArgument;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import io.github.zoltus.onecore.utils.ChatUtils;

import java.util.Arrays;

public class Msg implements ICommand {
    //todo /r response
    @Override
    public void init() {
        //msg <player> <message>
        ArgumentTree arg0 = new PlayerArgument();
        ArgumentTree arg1 = new ChatArgument(Lang.NODES_MESSAGE.getString())
                .usePreview(false)
                .withPreview((PreviewLegacy) info -> {
                    String[] args = info.fullInput().split(" ");
                    String target = args[1];
                    String sender = info.player().getName();
                    String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                    return ChatUtils.toComponents(toSendMessage(target, sender, message));
                })
                .executesPlayer((sender, args) -> {
            Player target = (Player) args[0];
            String senderName = sender.getName();
            String targetName = target.getName();
            String message = BaseComponent.toPlainText((BaseComponent[]) args[1]);
            message = ChatColor.translateAlternateColorCodes('&', message);
            String receivedMsg = Lang.MSG_RECEIVED_MSG.rp(IConfig.PLAYER_PH,senderName, IConfig.PLAYER2_PH, targetName, IConfig.MESSAGE_PH, message);

            sender.sendMessage(toSendMessage(targetName, senderName, message));
            target.sendMessage(receivedMsg);
        });
        //base
        new Command(Commands.MSG_LABEL)
                .withPermission(Commands.MSG_PERMISSION)
                .withAliases(Commands.MSG_ALIASES)
                .then(arg0.then(arg1))
                .override();
    }

    private String toSendMessage(String sender, String target, String message) {
        return Lang.MSG_SENT_MSG.rp(IConfig.PLAYER_PH, sender, IConfig.PLAYER2_PH, target, IConfig.MESSAGE_PH, message);
    }
}
