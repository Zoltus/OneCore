package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.ArgumentTree;
import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import io.github.zoltus.onecore.player.nbt.NBTPlayer;

public class God implements ICommand {


    @Override
    public void init() {
        //god <pelaaja>
        ArgumentTree arg0 = new OfflinePlayerArgument()
                .executes((sender, args) -> {
                    OfflinePlayer target = (OfflinePlayer) args[0];
                    boolean result;
                    if (target.getPlayer() != null) {
                        Player onlTarget = target.getPlayer();
                        result = !onlTarget.isInvulnerable();
                        onlTarget.setInvulnerable(result);
                        onlTarget.sendMessage(Lang.GOD_SET_TO.rp(IConfig.MODE_PH, result));
                    } else {
                        NBTPlayer nbtPlayer = new NBTPlayer(target);
                        result = !nbtPlayer.getInvulnerable();
                        nbtPlayer.setInvulnerable(result);
                        nbtPlayer.save();
                    }
                    if (sender != target.getPlayer()) {
                        String msg = Lang.GOD_CHANGED_TARGETS_GOD.rp(IConfig.PLAYER_PH, target.getName(), IConfig.MODE_PH, result);
                        sender.sendMessage(msg);
                    }
                });
        //god
        new Command(Commands.GOD_LABEL)
                .withPermission(Commands.GOD_PERMISSION)
                .withAliases(Commands.GOD_ALIASES)
                .executesPlayer((p, args) -> {
                    p.setInvulnerable(!p.isInvulnerable());
                    p.sendMessage(Lang.GOD_SET_TO.rp(IConfig.MODE_PH, p.isInvulnerable()));
                }).then(arg0).override();
    }
}

