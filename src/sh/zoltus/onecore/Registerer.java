package sh.zoltus.onecore;

import dev.jorel.commandapi.CommandAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import sh.zoltus.onecore.listeners.*;
import sh.zoltus.onecore.listeners.tweaks.Kick;
import sh.zoltus.onecore.listeners.tweaks.TeleportVelocity;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.commands.admin.*;
import sh.zoltus.onecore.player.teleporting.TeleportHandler;
import sh.zoltus.onecore.player.command.commands.regular.*;

import java.util.stream.Stream;

public class Registerer {
    //For unregistering on disable to have better reload support
    private final Listener[] listeners;
    private final IOneCommand[] cmds;
    private final OneCore plugin;

    public static Registerer init(OneCore plugin) {
        Registerer registerer = plugin.getRegisterer();
        return registerer == null ? new Registerer(plugin) : registerer;
    }

    private Registerer(OneCore plugin) {
        this.plugin = plugin;
        this.listeners = new Listener[]{
                new ChatColors(), new InvSeeHandler(),
                new JoinHandler(), new Kick(),
                new Mentions(), new PlayerJumpEvent(),
                new QuitListener(), new SignColorHandler(plugin),
                new TeleportHandler(), new TestListener(),
                new TeleportVelocity()
        };
        this.cmds = new IOneCommand[]{
                new Back(), new Broadcast(), new ClearChat(),
                new DelHome(), new Feed(),
                new Fly(), new Gamemode(), new God(),
                new Heal(), new Home(), new EnderChest(),
                new Invsee(), new KillAll(), new Msg(),
                new Ping(), new PlayTime(), new Reload(),
                new Repair(), new Rtp(), new Seen(),
                new SetHome(), new SetMaxPlayers(), new SetSpawn(),
                new SetWarp(), new SignEdit(), new Spawn(), new Speed(),
                new SystemInfo(), new Tp(), new Tpa(),
                new Tpaccept(), new TpaHere(), new TpDeny(),
                new TpToggle(), new Time(), new Top(),
                new Warp(), new Weather()
        };
        register();
    }


    private void register() {
        Bukkit.getConsoleSender().sendMessage("Registering command & listeners...");
        Stream.of(listeners).forEach(listener -> Bukkit.getServer().getPluginManager().registerEvents(listener, plugin));
        Stream.of(cmds).filter(IOneCommand::isEnabled).forEach(IOneCommand::register);
        if (plugin.getVault() != null) {
            new Economy().register();
        }
    }
}