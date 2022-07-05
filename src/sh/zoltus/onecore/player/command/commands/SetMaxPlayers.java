package sh.zoltus.onecore.player.command.commands;

import dev.jorel.commandapi.arguments.IntegerArgument;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.NODES_AMOUNT;

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
                    int maxPlayers = (int) args[0];
                    //todo switch to reflection for multiple 1.19 versions
                       /* DedicatedPlayerList server = ((CraftServer) Bukkit.getServer()).getHandle();
                        Field f = server.getClass().getSuperclass().getDeclaredField("f");
                        f.setAccessible(true);
                        f.set(server, maxPlayers);
                        p.sendMessage(SETMAXPLAYERS_SET.rp(AMOUNT_PH, maxPlayers));
                        catch
                          p.sendMessage("Error changing max players!")
                        */
                }),
        };
    }
}
