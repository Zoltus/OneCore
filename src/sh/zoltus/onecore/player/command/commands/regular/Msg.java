package sh.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.arguments.ChatArgument;
import dev.jorel.commandapi.wrappers.PreviewLegacy;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.PlayerArgument;

import java.util.Arrays;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.MESSAGE_PH;
import static sh.zoltus.onecore.data.configuration.yamls.Commands.PLAYER2_PH;
import static sh.zoltus.onecore.data.configuration.yamls.Commands.PLAYER_PH;
import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.*;

public class Msg implements IOneCommand {
    //todo /r response
    @Override
    public void init() {
        //msg <player>
        command(MSG_LABEL)
                .withPermission(MSG_PERMISSION)
                .withAliases(MSG_ALIASES)
                .withArguments(new PlayerArgument(), new ChatArgument(NODES_MESSAGE.getString())
                        .usePreview(false)
                        .withPreview((PreviewLegacy) info -> {
                            String[] args = info.fullInput().split(" ");
                            String target = args[1];
                            String sender = info.player().getName();
                            String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                            return toComponents(toSendMessage(target, sender, message));
                        }))
                .executesPlayer((sender, args) -> {
                    Player target = (Player) args[0];
                    String senderName = sender.getName();
                    String targetName = target.getName();
                    String message = BaseComponent.toPlainText((BaseComponent[]) args[1]);
                    message = ChatColor.translateAlternateColorCodes('&', message);
                    String receivedMsg = MSG_RECEIVED_MSG.rp(PLAYER_PH,senderName, PLAYER2_PH, targetName, MESSAGE_PH, message);

                    sender.sendMessage(toSendMessage(targetName, senderName, message));
                    target.sendMessage(receivedMsg);
                }).override();
    }

    private String toSendMessage(String sender, String target, String message) {
        return MSG_SENT_MSG.rp(PLAYER_PH, sender, PLAYER2_PH, target, MESSAGE_PH, message);
    }
}
