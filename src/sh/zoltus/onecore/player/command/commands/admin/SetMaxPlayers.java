package sh.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.IntegerArgument;
import org.bukkit.Bukkit;
import sh.zoltus.onecore.player.command.IOneCommand;

import java.lang.reflect.Field;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.NODES_AMOUNT;
import static sh.zoltus.onecore.configuration.yamls.Lang.SETMAXPLAYERS_SET;

public class SetMaxPlayers implements IOneCommand {

    @Override
    public void init() {
        //setmaxplayers <amount>
        command(SETMAXPLAYERS_LABEL)
                .withPermission(SETMAXPLAYERS_PERMISSION)
                .withAliases(SETMAXPLAYERS_ALIASES)
                .withArguments(new IntegerArgument(NODES_AMOUNT.getString()))
                .executesPlayer((p, args) -> {
                    try {
                        int maxPlayers = (int) args[0];
                        setMaxPlayers(maxPlayers);
                        p.sendMessage(SETMAXPLAYERS_SET.rp(AMOUNT_PH, maxPlayers));
                    } catch (ReflectiveOperationException e) {
                        p.sendMessage("Error changing max players!");
                    }
                }).register();
    }

    public static void setMaxPlayers(int amount) throws ReflectiveOperationException {
        String bukkitversion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
        Object playerlist = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".CraftServer").getDeclaredMethod("getHandle").invoke(Bukkit.getServer());
        Field maxplayers = playerlist.getClass().getSuperclass().getDeclaredField("f");
        maxplayers.setAccessible(true);
        maxplayers.set(playerlist, amount);
    }

    /*
        .executesPlayer((p, args) -> {
        int maxPlayers = (int) args[0];
        Bukkit.getServer().setMaxPlayers(maxPlayers);
        p.sendMessage(SETMAXPLAYERS_SET.rp(AMOUNT_PH, maxPlayers));
        }),
     */
}
