package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.IntegerArgument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;

import static io.github.zoltus.onecore.data.configuration.IConfig.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.SETMAXPLAYERS_SET;

public class SetMaxPlayers implements ICommand {

    @Override
    public void init() {
        //setmaxplayers <amount>
        new Command(Commands.SETMAXPLAYERS_LABEL)
                .withPermission(Commands.SETMAXPLAYERS_PERMISSION)
                .withAliases(Commands.SETMAXPLAYERS_ALIASES)
                .then(new IntegerArgument(Lang.NODES_AMOUNT.getString())
                        .executesPlayer((p, args) -> {
                            try {
                                int maxPlayers = (int) args.get(0);
                                setMaxPlayers(maxPlayers);
                                SETMAXPLAYERS_SET.send(p, AMOUNT_PH, maxPlayers);
                            } catch (ReflectiveOperationException e) {
                                p.sendMessage("Error changing max players!");
                            }
                        })).override();
    }

    public static void setMaxPlayers(int amount) throws ReflectiveOperationException {
        String bukkitversion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
        Object playerlist = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".CraftServer").getDeclaredMethod("getHandle").invoke(Bukkit.getServer());
        Field maxplayers = playerlist.getClass().getSuperclass().getDeclaredField("f");
        maxplayers.setAccessible(true);
        maxplayers.set(playerlist, amount);
    }
}
