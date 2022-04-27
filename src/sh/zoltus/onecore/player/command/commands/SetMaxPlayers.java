package sh.zoltus.onecore.player.command.commands;

import dev.jorel.commandapi.arguments.IntegerArgument;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;

import java.lang.reflect.Field;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.NODES_AMOUNT;
import static sh.zoltus.onecore.configuration.yamls.Lang.SETMAXPLAYERS_SET;

public class SetMaxPlayers implements IOneCommand {

    @Override
    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                //setmaxplayers <amount>
                command(SETMAXPLAYERS_LABEL)
                        .withPermission(SETMAXPLAYERS_PERMISSION)
                        .withAliases(SETMAXPLAYERS_ALIASES)
                        .withArguments(new IntegerArgument(NODES_AMOUNT.getString()))
                        .executesPlayer((p, args) -> {
                    try {
                        int maxPlayers = (int) args[0];
                        DedicatedPlayerList server = ((CraftServer) Bukkit.getServer()).getHandle();
                        Field f = server.getClass().getSuperclass().getDeclaredField("f");
                        f.setAccessible(true);
                        f.set(server, maxPlayers);
                        p.sendMessage(SETMAXPLAYERS_SET.rp(AMOUNT_PH, maxPlayers));
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        p.sendMessage("Error changing max players!");
                    }
                }),
        };
    }
}
