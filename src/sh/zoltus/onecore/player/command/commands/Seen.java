package sh.zoltus.onecore.player.command.commands;

import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.PlayerArgument;

import java.text.SimpleDateFormat;
import java.util.Date;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.SEEN_DATE_FORMAT;
import static sh.zoltus.onecore.configuration.yamls.Lang.SEEN_LAST_SEEN;

public class Seen implements IOneCommand {

    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                command(SEEN_LABEL)
                        .withPermission(SEEN_PERMISSION)
                        .withAliases(SEEN_ALIASES)
                        .withArguments(new PlayerArgument())
                        .executes((sender, args) -> {
                    Player oTarget = (Player) args[0];
                    SimpleDateFormat dateFormat = new SimpleDateFormat(SEEN_DATE_FORMAT.getString());
                    String dateString = dateFormat.format(new Date(oTarget.getFirstPlayed()));
                    sender.sendMessage(SEEN_LAST_SEEN.rp(PLAYER_PH, oTarget.getName(), TIME_PH, dateString));
                })};
    }
}
