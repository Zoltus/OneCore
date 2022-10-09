package sh.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.arguments.LocationArgument;
import org.bukkit.Location;
import sh.zoltus.onecore.player.command.Command;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.commands.regular.Spawn;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.NODES_LOCATION;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.SETSPAWN_SET;

public class SetSpawn implements IOneCommand {

    @Override
    public void init() {
        //setspawn <location>
        ArgumentTree arg0 = new LocationArgument(NODES_LOCATION.getString())
                .executesPlayer((p, args) -> {
                    Spawn.setSpawn((Location) args[0]);
                    p.sendMessage(SETSPAWN_SET.getString());
                });
        //setspawn
        new Command(SETSPAWN_LABEL)
                .withPermission(SETSPAWN_PERMISSION)
                .withAliases(SETSPAWN_ALIASES)
                .executesPlayer((p, args) -> {
                    Spawn.setSpawn(p.getLocation());
                    p.sendMessage(SETSPAWN_SET.getString());
                }).then(arg0).override();
    }
}
