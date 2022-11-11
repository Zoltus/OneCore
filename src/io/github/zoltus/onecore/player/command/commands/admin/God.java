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

import static io.github.zoltus.onecore.data.configuration.IConfig.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

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
                        GOD_SET_TO.send(onlTarget, MODE_PH, result);
                    } else {
                        NBTPlayer nbtPlayer = new NBTPlayer(target);
                        result = !nbtPlayer.getInvulnerable();
                        nbtPlayer.setInvulnerable(result);
                        nbtPlayer.save();
                    }
                    if (sender != target.getPlayer()) {
                        GOD_CHANGED_TARGETS_GOD.send(sender, PLAYER_PH, target.getName(), MODE_PH, result);
                    }
                });
        //god
        new Command(Commands.GOD_LABEL)
                .withPermission(Commands.GOD_PERMISSION)
                .withAliases(Commands.GOD_ALIASES)
                .executesPlayer((p, args) -> {
                    p.setInvulnerable(!p.isInvulnerable());
                    GOD_SET_TO.send(p, MODE_PH, p.isInvulnerable());
                }).then(arg0).override();
    }
}

