package sh.zoltus.onecore.player.command.commands.admin;

import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.PlayerArgument;

import java.text.SimpleDateFormat;
import java.util.Date;

import static sh.zoltus.onecore.data.configuration.yamls.Lang.SEEN_DATE_FORMAT;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.SEEN_LAST_SEEN;
import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;

public class Seen implements IOneCommand {

    @Override
    public void init() {
        command(SEEN_LABEL)
                .withPermission(SEEN_PERMISSION)
                .withAliases(SEEN_ALIASES)
                .withArguments(new PlayerArgument())
                .executes((sender, args) -> {
                    Player oTarget = (Player) args[0];
                    SimpleDateFormat dateFormat = new SimpleDateFormat(SEEN_DATE_FORMAT.getString());
                    String dateString = dateFormat.format(new Date(oTarget.getFirstPlayed()));
                    sender.sendMessage(SEEN_LAST_SEEN.rp(PLAYER_PH, oTarget.getName(), TIME_PH, dateString));
                }).override();
    }
}
