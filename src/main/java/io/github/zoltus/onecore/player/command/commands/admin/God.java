package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import io.github.zoltus.onecore.player.nbt.NBTPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class God implements ICommand {
    @Override
    public void init() {
        //god <pelaaja>
        Argument<?> arg0 = new OfflinePlayerArgument()
                .withPermission(Commands.GOD_PERMISSION_OTHER.asPermission())
                .executes((sender, args) -> {
                    OfflinePlayer target = (OfflinePlayer) args.get(0);
                    boolean result;
                    if (target.getPlayer() != null) {
                        Player onlTarget = target.getPlayer();
                        onlTarget.setInvulnerable(result = !onlTarget.isInvulnerable());
                        GOD_SELF.send(onlTarget, MODE_PH, result);
                    } else {
                        NBTPlayer nbtPlayer = new NBTPlayer(target);
                        nbtPlayer.setInvulnerable(result = !nbtPlayer.getInvulnerable());
                        nbtPlayer.save();
                    }
                    if (sender != target.getPlayer()) {
                        GOD_OTHER.send(sender, PLAYER_PH, target.getName(), MODE_PH, result);
                    }
                });
        //god
        new Command(Commands.GOD_LABEL)
                .withPermission(Commands.GOD_PERMISSION)
                .withAliases(Commands.GOD_ALIASES)
                .executesPlayer((p, args) -> {
                    p.setInvulnerable(!p.isInvulnerable());
                    GOD_SELF.send(p, MODE_PH, p.isInvulnerable());
                }).then(arg0).override();


        //Refresh vanished players action bar every 2.5s same for godmode?
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,
                () -> Bukkit.getOnlinePlayers().forEach(player -> {
                    if (player.isInvulnerable()) {
                        String actionbar = GOD_ACTION_BAR.getString();
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionbar));
                    }
                }), 0, 50);
    }
}

