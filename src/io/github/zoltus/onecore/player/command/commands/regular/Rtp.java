package io.github.zoltus.onecore.player.command.commands.regular;

import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;

import static io.github.zoltus.onecore.data.configuration.yamls.Commands.*;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class Rtp implements ICommand {

    private static final int RADIUS = Config.RTP_RADIUS.getInt();
    //todo /rtp <player>, todo rtp worker, max rtps per second to conig
    //every player in queue = teleport timer +1s
    @Override
    public void init() {
        new Command(RTP_LABEL)
                .withAliases(RTP_ALIASES)
                .withPermission(RTP_PERMISSION)
                .executesPlayer((p, args) -> {

                }).override();
    }



}
