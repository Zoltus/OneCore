package sh.zoltus.onecore.player.command.commands.regular;

import dev.jorel.commandapi.arguments.ChatArgument;
import dev.jorel.commandapi.wrappers.PreviewLegacy;
import dev.jorel.commandapi.wrappers.PreviewableFunction;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.PlayerArgument;

import static sh.zoltus.onecore.configuration.yamls.Commands.MESSAGE_PH;
import static sh.zoltus.onecore.configuration.yamls.Commands.PLAYER2_PH;
import static sh.zoltus.onecore.configuration.yamls.Commands.PLAYER_PH;
import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.*;

public class Msg implements IOneCommand {
    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                //msg <player>
                command(MSG_LABEL)
                        .withPermission(MSG_PERMISSION)
                        .withAliases(MSG_ALIASES)
                        .withArguments(new PlayerArgument(), new ChatArgument(NODES_MESSAGE.getString()).withPreview(getPreview()))
                        .executesPlayer((sender, args) -> {
                    Player target = (Player) args[0];
                    String message = BaseComponent.toPlainText((BaseComponent[]) args[1]);
                    String sentMsg = MSG_SENT_MSG.rp(PLAYER_PH, sender.getName(), PLAYER2_PH, target.getName(), MESSAGE_PH, message);
                    sender.sendMessage(sentMsg);
                    String receivedMsg = MSG_RECEIVED_MSG.rp(PLAYER_PH, sender.getName(), PLAYER2_PH, target.getName(), MESSAGE_PH, message);
                    target.sendMessage(receivedMsg);
                })
        };
    }

    private PreviewableFunction<BaseComponent[]> getPreview() {
        return (PreviewLegacy) info -> {
            String target = info.fullInput().split(" ")[1];
            String message = info.input();
            String sentMsg = MSG_SENT_MSG.rp(PLAYER_PH, info.player().getName(), PLAYER2_PH, target, MESSAGE_PH, message);
            return toComponents(sentMsg);
        };
    }
}
