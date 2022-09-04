package sh.zoltus.onecore;

import org.bukkit.Bukkit;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.commands.admin.*;
import sh.zoltus.onecore.player.command.commands.regular.*;

import java.util.List;

public class CommandHandler {
    private final OneCore plugin;

    private final List<Class<? extends IOneCommand>> cmds = List.of(
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

    public static CommandHandler Register(OneCore plugin) {
        CommandHandler handler = plugin.getCommandHandler();
        return handler == null ? new CommandHandler(plugin) : handler;
    }

    private CommandHandler(OneCore plugin) {
        this.plugin = plugin;
        Bukkit.getConsoleSender().sendMessage("Registering commands...");
        registerCommands();
    }


    private void registerCommands() {
        //Registers economy commands only if vault has been loaded successfully
        if (plugin.getVault() != null) {
            new Economy().register();
        }
        //Creates instanceof the class if its enabled and then registers it.
        //faster startup when disabling cmds
        cmds.stream().filter(IOneCommand::isEnabled)
                .forEach(clazz -> {
                    try {
                        IOneCommand iOneCommand = clazz.getDeclaredConstructor().newInstance();
                        iOneCommand.register();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

}