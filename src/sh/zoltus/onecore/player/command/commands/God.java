package sh.zoltus.onecore.player.command.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import sh.zoltus.onecore.utils.NBTPlayer;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.GOD_CHANGED_TARGETS_GOD;
import static sh.zoltus.onecore.configuration.yamls.Lang.GOD_SET_TO;

public class God implements IOneCommand {


    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                //god
                command(GOD_LABEL)
                        .withPermission(GOD_PERMISSION)
                        .withAliases(GOD_ALIASES)
                        .executesPlayer((p, args) -> {
                    p.setInvulnerable(!p.isInvulnerable());
                    p.sendMessage(GOD_SET_TO.rp(MODE_PH, p.isInvulnerable()));
                }),
                //god <pelaaja>
                command(GOD_LABEL)
                        .withPermission(GOD_PERMISSION)
                        .withAliases(GOD_ALIASES)
                        .withArguments(new OfflinePlayerArgument())
                        .executes((sender, args) -> {
                    OfflinePlayer target = (OfflinePlayer) args[0];
                    boolean result;
                    if (target.getPlayer() != null) {
                        Player onlTarget = target.getPlayer();
                        result = !onlTarget.isInvulnerable();
                        onlTarget.setInvulnerable(result);
                        onlTarget.sendMessage(GOD_SET_TO.rp(MODE_PH, result));
                    } else {
                        NBTPlayer nbtPlayer = new NBTPlayer(target);
                        result = !nbtPlayer.getInvulnerable();
                        nbtPlayer.setInvulnerable(result);
                        nbtPlayer.save();
                    }
                    if (sender != target.getPlayer()) {
                        String msg = GOD_CHANGED_TARGETS_GOD.rp(PLAYER_PH, target.getName(), MODE_PH, result);
                        sender.sendMessage(msg);
                    }
                })
        };
    }
}

