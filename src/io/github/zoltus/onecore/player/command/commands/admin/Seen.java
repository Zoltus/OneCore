package io.github.zoltus.onecore.player.command.commands.admin;

import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.PlayerArgument;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Seen implements ICommand {

    @Override
    public void init() {
        new Command(Commands.SEEN_LABEL)
                .withPermission(Commands.SEEN_PERMISSION)
                .withAliases(Commands.SEEN_ALIASES)
                .then(new PlayerArgument()
                        .executes((sender, args) -> {
                            Player oTarget = (Player) args[0];
                            SimpleDateFormat dateFormat = new SimpleDateFormat(Lang.SEEN_DATE_FORMAT.getString());
                            String dateString = dateFormat.format(new Date(oTarget.getFirstPlayed()));
                            sender.sendMessage(Lang.SEEN_LAST_SEEN.rp(IConfig.PLAYER_PH, oTarget.getName(), IConfig.TIME_PH, dateString));
                        })).override();
    }
}
