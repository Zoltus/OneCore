package io.github.zoltus.onecore.player.command.commands.admin;

import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import org.bukkit.OfflinePlayer;

import java.text.SimpleDateFormat;
import java.util.Date;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;
import static io.github.zoltus.onecore.data.configuration.PlaceHolder.*;

public class Seen implements ICommand {

    @Override
    public void init() {
        new Command(Commands.SEEN_LABEL)
                .withPermission(Commands.SEEN_PERMISSION)
                .withAliases(Commands.SEEN_ALIASES)
                .then(new OfflinePlayerArgument()
                        .executes((sender, args) -> {
                            OfflinePlayer oTarget = (OfflinePlayer) args.get(0);
                            SimpleDateFormat dateFormat = new SimpleDateFormat(SEEN_DATE_FORMAT.get());
                            String dateString = dateFormat.format(new Date(oTarget.getLastPlayed()));
                            SEEN_LAST_SEEN.rb(PLAYER_PH, oTarget.getName())
                                    .rb(TIME_PH, dateString)
                                    .send(sender);
                        })).override();
    }
}
