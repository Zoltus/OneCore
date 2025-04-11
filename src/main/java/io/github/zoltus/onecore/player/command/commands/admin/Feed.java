package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import io.github.zoltus.onecore.player.nbt.NBTPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static io.github.zoltus.onecore.data.configuration.IConfig.PLAYER_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.FEED_YOU_FED_TARGET;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.FEED_YOU_HAVE_BEEN_HEALED;

public class Feed implements ICommand {
    @Override
    public void init() {
        //feed <player>
        Argument<?> arg0 = new OfflinePlayerArgument()
                .withPermission(Commands.FEED_PERMISSION_OTHER.asPermission())
                .executes((sender, args) -> {
                    OfflinePlayer target = (OfflinePlayer) args.get(0);
                    feed(target);
                    if (target.getPlayer() != sender) {
                        FEED_YOU_FED_TARGET.rb(PLAYER_PH, target.getName()).send(sender);
                    }
                });
        //feed
        new Command(Commands.FEED_LABEL)
                .withPermission(Commands.FEED_PERMISSION)
                .withAliases(Commands.FEED_ALIASES)
                .executesPlayer((player, args) -> {
                    feed(player);
                }).then(arg0).override();
    }

    // Feeds player
    private void feed(OfflinePlayer offP) {
        if (offP.getPlayer() != null) {
            Player p = offP.getPlayer();
            p.setFoodLevel(20);
            p.setSaturation(20.0F);
            p.setExhaustion(0.0f);
            FEED_YOU_HAVE_BEEN_HEALED.send(p);
        } else {
            NBTPlayer nbtPlayer = new NBTPlayer(offP);
            nbtPlayer.setFoodLevel(20);
            nbtPlayer.setSaturationLevel(20.0F);
            nbtPlayer.setExhaustionLevel(0.0F);
            nbtPlayer.save();
        }
    }
}
