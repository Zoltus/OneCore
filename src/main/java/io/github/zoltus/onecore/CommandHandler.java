package io.github.zoltus.onecore;

import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.commands.admin.*;
import io.github.zoltus.onecore.player.command.commands.economy.Ecomomy;
import io.github.zoltus.onecore.player.command.commands.regular.*;

import java.util.List;

@SuppressWarnings("InstantiationOfUtilityClass")
public class CommandHandler {

    public static CommandHandler register(OneCore plugin) {
        CommandHandler handler = plugin.getCommandHandler();
        return handler == null ? new CommandHandler(plugin) : handler;
    }

    private CommandHandler(OneCore plugin) {
        plugin.getLogger().info("Registering commands...");
        //Register eco commands only if 1. Vault isnt null 2. Economy hook is enabled, 3. EconomyCMD is enabled
        if (plugin.getVault() != null
                && Config.ECONOMY_HOOK.getBoolean()
                && ICommand.isEnabled(Ecomomy.class)) {
            new Ecomomy().register();
        }
        //Creates instanceof the class if its enabled and then registers it.
        //faster startup when disabling cmds
        List<Class<? extends ICommand>> cmds = List.of(
                Back.class, Broadcast.class, ClearChat.class,
                DelHome.class, Feed.class,
                Fly.class, Gamemode.class, God.class,
                Heal.class, Home.class, EnderChest.class,
                Invsee.class, KillAll.class, Msg.class,
                Ping.class, PlayTime.class, Reload.class,
                Repair.class, Seen.class,
                SetHome.class, SetMaxPlayers.class, SetSpawn.class, SetFirstJoinSpawn.class,
                SetWarp.class, SignEdit.class, Spawn.class, Speed.class,
                SystemInfo.class, Tp.class, Tpa.class,
                Tpaccept.class, TpaHere.class, TpDeny.class,
                TpToggle.class, Time.class, Top.class,
                Warp.class, Weather.class
        );
        for (Class<? extends ICommand> cmd : cmds) {
            if (ICommand.isEnabled(cmd)) {
                try {
                    ICommand iCommand = cmd.getDeclaredConstructor().newInstance();
                    iCommand.register();
                } catch (Exception e) {
                    throw new CommandException("cmd: " + cmd.getName() + e.getMessage());
                }
            }
        }
    }

    private static class CommandException extends RuntimeException {
        public CommandException(String message) {
            super(message);
        }
    }
}