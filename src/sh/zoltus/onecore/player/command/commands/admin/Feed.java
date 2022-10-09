package sh.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.ArgumentTree;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.Command;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import sh.zoltus.onecore.player.nbt.NBTPlayer;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.FEED_YOU_FED_TARGET;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.FEED_YOU_HAVE_BEEN_HEALED;

public class Feed implements IOneCommand {
    @Override
    public void init() {
        //feed <player>
        ArgumentTree arg0 = new OfflinePlayerArgument()
                .executes((sender, args) -> {
                    OfflinePlayer target = (OfflinePlayer) args[0];
                    feed(target);
                    if (target.getPlayer() != sender) {
                        sender.sendMessage(FEED_YOU_FED_TARGET.rp(PLAYER_PH, target.getName()));
                    }
                });
        //feed
        new Command(FEED_LABEL)
                .withPermission(FEED_PERMISSION)
                .withAliases(FEED_ALIASES)
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
            p.sendMessage(FEED_YOU_HAVE_BEEN_HEALED.getString());
        } else {
            NBTPlayer nbtPlayer = new NBTPlayer(offP);
            nbtPlayer.setfoodLevel(20);
            nbtPlayer.setfoodSaturationLevel(20.0F);
            nbtPlayer.setFoodExhaustionLevel(0.0F);
            nbtPlayer.save();
        }
    }
}
