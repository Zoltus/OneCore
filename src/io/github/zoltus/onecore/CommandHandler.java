package io.github.zoltus.onecore;

import io.github.zoltus.onecore.player.command.IOneCommand;
import io.github.zoltus.onecore.player.command.commands.admin.*;
import io.github.zoltus.onecore.player.command.commands.regular.*;
import sh.zoltus.onecore.player.command.commands.admin.*;
import sh.zoltus.onecore.player.command.commands.regular.*;

import java.util.List;

@SuppressWarnings("InstantiationOfUtilityClass")
public class CommandHandler {

    public static CommandHandler register(OneCore plugin) {
        CommandHandler handler = plugin.getCommandHandler();
        return handler == null ? new CommandHandler(plugin) : handler;
    }

    private CommandHandler(OneCore plugin) {
        plugin.getLogger().info("Registering commands...");
        //Registers economy commands only if vault has been loaded successfully
        if (plugin.getVault() != null) {
            new EconomyCMD().register();
        }
        //Creates instanceof the class if its enabled and then registers it.
        //faster startup when disabling cmds
        List<Class<? extends IOneCommand>> cmds = List.of(
                Back.class, Broadcast.class, ClearChat.class,
                DelHome.class, Feed.class,
                Fly.class, Gamemode.class, God.class,
                Heal.class, Home.class, EnderChest.class,
                Invsee.class, KillAll.class, Msg.class,
                Ping.class, PlayTime.class, Reload.class,
                Repair.class, Rtp.class, Seen.class,
                SetHome.class, SetMaxPlayers.class, SetSpawn.class,
                SetWarp.class, SignEdit.class, Spawn.class, Speed.class,
                SystemInfo.class, Tp.class, Tpa.class,
                Tpaccept.class, TpaHere.class, TpDeny.class,
                TpToggle.class, Time.class, Top.class,
                Warp.class, Weather.class
        );
        for (Class<? extends IOneCommand> cmd : cmds) {
            if (IOneCommand.isEnabled(cmd)) {
                try {
                    IOneCommand iOneCommand = cmd.getDeclaredConstructor().newInstance();
                    iOneCommand.register();
                } catch (Exception e) {
                    throw new RuntimeException("cmd: " + cmd.getName() + e.getMessage());
                }
            }
        }
    }
}